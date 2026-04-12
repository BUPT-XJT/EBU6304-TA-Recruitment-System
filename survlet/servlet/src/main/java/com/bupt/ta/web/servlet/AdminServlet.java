package com.bupt.ta.web.servlet;

import com.bupt.ta.web.Application;
import com.bupt.ta.web.ApplicationService;
import com.bupt.ta.web.Position;
import com.bupt.ta.web.PositionService;
import com.bupt.ta.web.StatsUtil;
import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebAuth.requireRole(req, resp, userService, "ADMIN")) return;

        User user = WebAuth.getUser(req, userService);
        String path = req.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        req.setAttribute("currentUser", user);
        req.setAttribute("sidebarRole", "ADMIN");

        List<Position> allPos = positionService.getAllPositions();
        List<Application> allApps = applicationService.getAllApplications();
        List<User> allUsers = userService.getAllUsers();
        Map<String, Object> stats = StatsUtil.compute(userService, positionService, applicationService);

        switch (path) {
            case "/dashboard":
                req.setAttribute("sidebarActive", "dashboard");
                req.setAttribute("stats", stats);
                req.setAttribute("pendingPositions", positionService.getPendingPositions());
                req.setAttribute("recentApplications", allApps);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
                break;
            case "/approve": {
                req.setAttribute("sidebarActive", "approve");
                String tabRaw = req.getParameter("tab");
                final String tab = tabRaw == null ? "PENDING" : tabRaw;
                List<Position> list;
                if ("ALL".equals(tab)) list = allPos;
                else list = allPos.stream().filter(pos -> tab.equals(pos.getStatus())).collect(Collectors.toList());
                req.setAttribute("positions", list);
                req.setAttribute("approveTab", tab);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/approve.jsp").forward(req, resp);
                break;
            }
            case "/stats":
                req.setAttribute("sidebarActive", "stats");
                req.setAttribute("stats", stats);
                req.setAttribute("positions", allPos);
                req.setAttribute("applications", allApps);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/stats.jsp").forward(req, resp);
                break;
            case "/workload": {
                req.setAttribute("sidebarActive", "workload");
                List<User> tas = allUsers.stream().filter(u -> "TA".equals(u.getRole())).collect(Collectors.toList());
                List<Map<String, Object>> rows = new ArrayList<>();
                int maxHours = 20;
                for (User ta : tas) {
                    List<Application> assigned = allApps.stream()
                            .filter(a -> ta.getId().equals(a.getTaId()) && "PASSED".equals(a.getStatus()))
                            .collect(Collectors.toList());
                    int totalHours = 0;
                    for (Application a : assigned) {
                        Position p = positionService.getPositionById(a.getPositionId());
                        if (p != null) totalHours += p.getHoursPerWeek();
                    }
                    double pct = Math.min((totalHours * 100.0) / maxHours, 100.0);
                    String barColor = pct > 80 ? "var(--danger)" : pct > 50 ? "var(--warning)" : "var(--success)";
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("ta", ta);
                    row.put("assignedCount", assigned.size());
                    row.put("totalHours", totalHours);
                    row.put("pct", pct);
                    row.put("barColor", barColor);
                    rows.add(row);
                }
                req.setAttribute("workloadRows", rows);
                req.setAttribute("maxHours", maxHours);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(req, resp);
                break;
            }
            case "/users": {
                req.setAttribute("sidebarActive", "users");
                String roleRaw = req.getParameter("role");
                final String role = roleRaw == null ? "ALL" : roleRaw;
                String qRaw = req.getParameter("q");
                final String q = qRaw == null ? "" : qRaw;
                List<User> list = allUsers;
                if (!"ALL".equals(role)) {
                    list = allUsers.stream().filter(usr -> role.equals(usr.getRole())).collect(Collectors.toList());
                }
                if (!q.isEmpty()) {
                    final String qq = q.toLowerCase();
                    list = list.stream()
                            .filter(usr -> (usr.getName() + " " + usr.getId() + " " + usr.getEmail()).toLowerCase().contains(qq))
                            .collect(Collectors.toList());
                }
                req.setAttribute("users", list);
                req.setAttribute("userFilterRole", role);
                req.setAttribute("userFilterQ", q);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
                break;
            }
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!WebAuth.requireRole(req, resp, userService, "ADMIN")) return;
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/approve")) {
            String positionId = trim(req.getParameter("positionId"));
            String status = trim(req.getParameter("status"));
            positionService.updatePositionStatus(positionId, status);
            req.getSession().setAttribute("flashMsg", "Position " + status.toLowerCase());
            String tab = trim(req.getParameter("returnTab"));
            if (tab.isEmpty()) tab = "PENDING";
            resp.sendRedirect(req.getContextPath() + "/admin/approve?tab=" + tab);
            return;
        }

        resp.sendError(404);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
