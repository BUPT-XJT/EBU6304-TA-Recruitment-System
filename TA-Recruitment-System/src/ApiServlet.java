import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ApiServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ApiServlet.class.getName());
    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    // ========== HTTP方法处理 ==========
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();

        try {
            switch (path) {
                case "/session" -> handleSession(req, resp);
                case "/positions" -> handleGetPositions(req, resp);
                case "/applications" -> handleGetApplications(req, resp);
                case "/users" -> handleGetUsers(req, resp);
                case "/stats" -> handleGetStats(req, resp);
                default -> {
                    if (path.startsWith("/positions/")) handleGetPosition(req, resp, path);
                    else if (path.startsWith("/users/")) handleGetUser(req, resp, path);
                    else sendError(resp, 404, "Not found");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "DoGet failed: path=" + path, e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();

        try {
            switch (path) {
                case "/login" -> handleLogin(req, resp);
                case "/register" -> handleRegister(req, resp);
                case "/logout" -> handleLogout(req, resp);
                case "/positions" -> handleCreatePosition(req, resp);
                case "/applications" -> handleCreateApplication(req, resp);
                default -> sendError(resp, 404, "Not found");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "DoPost failed: path=" + path, e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();

        try {
            if (path.matches("/positions/.+/status")) handleUpdatePositionStatus(req, resp, path);
            else if (path.matches("/applications/.+/status")) handleUpdateApplicationStatus(req, resp, path);
            else if (path.matches("/users/.+/password")) handleChangePassword(req, resp, path);
            else if (path.matches("/users/.+")) handleUpdateUser(req, resp, path);
            else sendError(resp, 404, "Not found");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "DoPut failed: path=" + path, e);
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(200);
    }

    // ========== 权限/会话相关 ==========
    /**
     * 处理用户登录请求
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        String userId = JsonUtil.getJsonString(body, "userId");
        String password = JsonUtil.getJsonString(body, "password");

        User user = userService.login(userId, password);
        if (user == null) {
            sendError(resp, 401, "Invalid credentials");
            return;
        }
        // 初始化会话
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(user)));
    }

    /**
     * 处理用户注册请求（仅TA角色）
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        // 解析注册参数
        String name = JsonUtil.getJsonString(body, "name");
        String password = JsonUtil.getJsonString(body, "password");
        String email = JsonUtil.getJsonString(body, "email");
        String phone = JsonUtil.getJsonString(body, "phone");
        String programme = JsonUtil.getJsonString(body, "programme");

        // 生成TA ID
        String id = "TA" + String.format("%03d", userService.generateTAId());
        User user = new User(id, name, password, "TA", email, phone, programme, "", "", "", "", "", "");
        boolean ok = userService.register(user);

        if (!ok) {
            sendError(resp, 409, "Email already exists");
            return;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("userId", id);
        sendJson(resp, JsonUtil.toJson(result));
    }

    /**
     * 处理用户登出请求
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", true);
        sendJson(resp, JsonUtil.toJson(r));
    }

    /**
     * 校验会话有效性并返回当前登录用户信息
     */
    private void handleSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 校验会话
        User loginUser = getLoginUser(req);
        if (loginUser == null) {
            sendError(resp, 401, "Not logged in");
            return;
        }
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(loginUser)));
    }

    // ========== 工具方法 ==========
    /**
     * 获取当前登录用户（会话校验）
     * @return 登录用户，未登录/用户不存在返回null
     */
    private User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return null;
        }
        String userId = (String) session.getAttribute("userId");
        return userService.getUserById(userId);
    }

    /**
     * 设置跨域响应头
     */
    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * 发送JSON响应
     */
    private void sendJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    /**
     * 发送错误JSON响应
     */
    private void sendError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("error", msg);
        sendJson(resp, JsonUtil.toJson(err));
    }

    /**
     * 安全转换整数（失败返回默认值）
     */
    private int parseInt(String s, int def) {
        try {
            return s == null ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.warning("Parse int failed: s=" + s + ", use default=" + def);
            return def;
        }
    }
