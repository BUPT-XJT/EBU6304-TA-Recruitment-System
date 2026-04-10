import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

public class ApiServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            if (path.equals("/session")) {
                handleSession(req, resp);
            } else if (path.equals("/positions")) {
                handleGetPositions(req, resp);
            } else if (path.startsWith("/positions/")) {
                handleGetPosition(req, resp, path);
            } else if (path.equals("/applications")) {
                handleGetApplications(req, resp);
            } else if (path.equals("/users")) {
                handleGetUsers(req, resp);
            } else if (path.startsWith("/users/")) {
                handleGetUser(req, resp, path);
            } else if (path.equals("/stats")) {
                handleGetStats(req, resp);
            } else {
                sendError(resp, 404, "Not found");
            }
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            if (path.equals("/login")) {
                handleLogin(req, resp);
            } else if (path.equals("/register")) {
                handleRegister(req, resp);
            } else if (path.equals("/logout")) {
                handleLogout(req, resp);
            } else if (path.equals("/positions")) {
                handleCreatePosition(req, resp);
            } else if (path.equals("/applications")) {
                handleCreateApplication(req, resp);
            } else {
                sendError(resp, 404, "Not found");
            }
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            if (path.matches("/positions/.+/status")) {
                handleUpdatePositionStatus(req, resp, path);
            } else if (path.matches("/applications/.+/status")) {
                handleUpdateApplicationStatus(req, resp, path);
            } else if (path.matches("/users/.+/password")) {
                handleChangePassword(req, resp, path);
            } else if (path.matches("/users/.+")) {
                handleUpdateUser(req, resp, path);
            } else {
                sendError(resp, 404, "Not found");
            }
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(200);
    }

    // ==================== Auth ====================

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        String userId = JsonUtil.getJsonString(body, "userId");
        String password = JsonUtil.getJsonString(body, "password");
        User user = userService.login(userId, password);
        if (user == null) {
            sendError(resp, 401, "Invalid credentials");
            return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(user)));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        String name = JsonUtil.getJsonString(body, "name");
        String password = JsonUtil.getJsonString(body, "password");
        String email = JsonUtil.getJsonString(body, "email");
        String phone = JsonUtil.getJsonString(body, "phone");
        String programme = JsonUtil.getJsonString(body, "programme");
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

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", true);
        sendJson(resp, JsonUtil.toJson(r));
    }

    private void handleSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(resp, 401, "Not logged in");
            return;
        }
        String userId = (String) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        if (user == null) {
            sendError(resp, 401, "User not found");
            return;
        }
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(user)));
    }

    // ==================== Positions ====================

    private void handleGetPositions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String status = req.getParameter("status");
        String moId = req.getParameter("moId");
        List<Position> positions;
        if (moId != null && !moId.isEmpty()) {
            positions = positionService.getPositionsByMo(moId);
        } else if ("APPROVED".equals(status)) {
            positions = positionService.getApprovedPositions();
        } else if ("PENDING".equals(status)) {
            positions = positionService.getPendingPositions();
        } else {
            positions = positionService.getAllPositions();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Position p : positions) list.add(JsonUtil.positionToMap(p));
        sendJson(resp, JsonUtil.toJsonArray(list));
    }

    private void handleGetPosition(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String id = path.replace("/positions/", "");
        Position p = positionService.getPositionById(id);
        if (p == null) { sendError(resp, 404, "Position not found"); return; }
        sendJson(resp, JsonUtil.toJson(JsonUtil.positionToMap(p)));
    }

    private void handleCreatePosition(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        String moId = JsonUtil.getJsonString(body, "moId");
        String moName = JsonUtil.getJsonString(body, "moName");
        String courseName = JsonUtil.getJsonString(body, "courseName");
        String courseCode = JsonUtil.getJsonString(body, "courseCode");
        String department = JsonUtil.getJsonString(body, "department");
        int numPositions = parseInt(JsonUtil.getJsonString(body, "numPositions"), 1);
        int hoursPerWeek = parseInt(JsonUtil.getJsonString(body, "hoursPerWeek"), 0);
        String payRate = JsonUtil.getJsonString(body, "payRate");
        String deadline = JsonUtil.getJsonString(body, "deadline");
        String requiredSkills = JsonUtil.getJsonString(body, "requiredSkills");
        String duties = JsonUtil.getJsonString(body, "duties");

        String id = "POS" + String.format("%03d", positionService.generateId());
        Position pos = new Position(id, moId, moName, courseName, courseCode, department,
                numPositions, hoursPerWeek, payRate, deadline, requiredSkills, duties, "PENDING");
        positionService.addPosition(pos);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("id", id);
        sendJson(resp, JsonUtil.toJson(result));
    }

    private void handleUpdatePositionStatus(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String id = path.split("/")[2];
        String body = JsonUtil.readBody(req);
        String status = JsonUtil.getJsonString(body, "status");
        boolean ok = positionService.updatePositionStatus(id, status);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", ok);
        sendJson(resp, JsonUtil.toJson(r));
    }

    // ==================== Applications ====================

    private void handleGetApplications(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String taId = req.getParameter("taId");
        String positionId = req.getParameter("positionId");
        List<Application> apps;
        if (taId != null && !taId.isEmpty()) {
            apps = applicationService.getApplicationsByUser(taId);
        } else if (positionId != null && !positionId.isEmpty()) {
            apps = applicationService.getApplicationsByPosition(positionId);
        } else {
            apps = applicationService.getAllApplications();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Application a : apps) list.add(JsonUtil.applicationToMap(a));
        sendJson(resp, JsonUtil.toJsonArray(list));
    }

    private void handleCreateApplication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = JsonUtil.readBody(req);
        String taId = JsonUtil.getJsonString(body, "taId");
        String taName = JsonUtil.getJsonString(body, "taName");
        String positionId = JsonUtil.getJsonString(body, "positionId");
        String positionTitle = JsonUtil.getJsonString(body, "positionTitle");
        String courseCode = JsonUtil.getJsonString(body, "courseCode");

        if (applicationService.hasApplied(taId, positionId)) {
            sendError(resp, 409, "Already applied");
            return;
        }

        String id = "APP" + String.format("%03d", applicationService.generateId());
        String date = LocalDate.now().toString();
        Application app = new Application(id, taId, taName, positionId, positionTitle, courseCode, date, "PENDING", "Under review");
        applicationService.createApplication(app);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("id", id);
        sendJson(resp, JsonUtil.toJson(result));
    }

    private void handleUpdateApplicationStatus(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String id = path.split("/")[2];
        String body = JsonUtil.readBody(req);
        String status = JsonUtil.getJsonString(body, "status");
        String feedback = JsonUtil.getJsonString(body, "feedback");
        boolean ok = applicationService.updateApplicationStatus(id, status, feedback);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", ok);
        sendJson(resp, JsonUtil.toJson(r));
    }

    // ==================== Users ====================

    private void handleGetUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = req.getParameter("role");
        List<User> users;
        if (role != null && !role.isEmpty()) {
            users = new ArrayList<>();
            for (User u : userService.getAllUsers()) {
                if (u.getRole().equals(role)) users.add(u);
            }
        } else {
            users = userService.getAllUsers();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users) list.add(JsonUtil.userToMap(u));
        sendJson(resp, JsonUtil.toJsonArray(list));
    }

    private void handleGetUser(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String rawId = path.replace("/users/", "");
        if (rawId.contains("/")) return;
        User u = userService.getUserById(rawId);
        if (u == null) { sendError(resp, 404, "User not found"); return; }
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(u)));
    }

    private void handleUpdateUser(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String id = path.replace("/users/", "");
        if (id.contains("/")) return;
        User existing = userService.getUserById(id);
        if (existing == null) { sendError(resp, 404, "User not found"); return; }

        String body = JsonUtil.readBody(req);
        String name = JsonUtil.getJsonString(body, "name");
        if (!name.isEmpty()) existing.setName(name);
        String phone = JsonUtil.getJsonString(body, "phone");
        if (!phone.isEmpty()) existing.setPhone(phone);
        String programme = JsonUtil.getJsonString(body, "programme");
        if (!programme.isEmpty()) existing.setProgramme(programme);
        String yearOfStudy = JsonUtil.getJsonString(body, "yearOfStudy");
        if (!yearOfStudy.isEmpty()) existing.setYearOfStudy(yearOfStudy);
        String skills = JsonUtil.getJsonString(body, "skills");
        if (!skills.isEmpty()) existing.setSkills(skills);
        String experience = JsonUtil.getJsonString(body, "experience");
        if (!experience.isEmpty()) existing.setExperience(experience);
        String university = JsonUtil.getJsonString(body, "university");
        if (!university.isEmpty()) existing.setUniversity(university);
        String gpa = JsonUtil.getJsonString(body, "gpa");
        if (!gpa.isEmpty()) existing.setGpa(gpa);

        userService.updateUser(existing);
        sendJson(resp, JsonUtil.toJson(JsonUtil.userToMap(existing)));
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        String id = path.split("/")[2];
        String body = JsonUtil.readBody(req);
        String oldPwd = JsonUtil.getJsonString(body, "oldPassword");
        String newPwd = JsonUtil.getJsonString(body, "newPassword");
        boolean ok = userService.changePassword(id, oldPwd, newPwd);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", ok);
        if (!ok) r.put("message", "Old password incorrect");
        sendJson(resp, JsonUtil.toJson(r));
    }

    // ==================== Stats ====================

    private void handleGetStats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<Position> allPos = positionService.getAllPositions();
        List<Application> allApps = applicationService.getAllApplications();
        List<User> allUsers = userService.getAllUsers();

        stats.put("totalPositions", allPos.size());
        int approved = 0, pending = 0;
        for (Position p : allPos) {
            if ("APPROVED".equals(p.getStatus())) approved++;
            if ("PENDING".equals(p.getStatus())) pending++;
        }
        stats.put("approvedPositions", approved);
        stats.put("pendingPositions", pending);
        stats.put("totalApplications", allApps.size());

        int appPending = 0, appPassed = 0, appFailed = 0;
        for (Application a : allApps) {
            if ("PENDING".equals(a.getStatus())) appPending++;
            if ("PASSED".equals(a.getStatus())) appPassed++;
            if ("FAILED".equals(a.getStatus())) appFailed++;
        }
        stats.put("pendingApplications", appPending);
        stats.put("passedApplications", appPassed);
        stats.put("failedApplications", appFailed);

        int taCount = 0, moCount = 0;
        for (User u : allUsers) {
            if ("TA".equals(u.getRole())) taCount++;
            if ("MO".equals(u.getRole())) moCount++;
        }
        stats.put("totalTAs", taCount);
        stats.put("totalMOs", moCount);
        stats.put("totalUsers", allUsers.size());

        sendJson(resp, JsonUtil.toJson(stats));
    }

    // ==================== Helpers ====================

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    private void sendError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("error", msg);
        sendJson(resp, JsonUtil.toJson(err));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
