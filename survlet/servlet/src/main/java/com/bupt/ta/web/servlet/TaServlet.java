package com.bupt.ta.web.servlet;

import com.bupt.ta.web.Application;
import com.bupt.ta.web.ApplicationService;
import com.bupt.ta.web.Position;
import com.bupt.ta.web.PositionService;
import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebAuth.requireRole(req, resp, userService, "TA")) return;

        User user = WebAuth.getUser(req, userService);
        String path = req.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/ta/dashboard");
            return;
        }

        req.setAttribute("currentUser", user);
        req.setAttribute("sidebarRole", "TA");

        switch (path) {
            case "/dashboard":
                req.setAttribute("sidebarActive", "dashboard");
                List<Application> apps = applicationService.getApplicationsByUser(user.getId());
                List<Position> approved = positionService.getApprovedPositions();
                req.setAttribute("applications", apps);
                req.setAttribute("approvedPositions", approved);
                req.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(req, resp);
                break;
            case "/profile":
                req.setAttribute("sidebarActive", "profile");
                req.setAttribute("profileUser", userService.getUserById(user.getId()));
                req.getRequestDispatcher("/WEB-INF/jsp/ta/profile.jsp").forward(req, resp);
                break;
            case "/positions": {
                req.setAttribute("sidebarActive", "positions");
                String q = req.getParameter("q");
                String dept = req.getParameter("dept");
                List<Position> all = positionService.getApprovedPositions();
                List<Application> mine = applicationService.getApplicationsByUser(user.getId());
                Set<String> appliedIds = mine.stream().map(Application::getPositionId).collect(Collectors.toSet());
                List<Position> filtered = new ArrayList<>();
                for (Position p : all) {
                    if (dept != null && !dept.isEmpty() && !dept.equals(p.getDepartment())) continue;
                    if (q != null && !q.isEmpty()) {
                        String qq = q.toLowerCase();
                        String blob = (p.getCourseName() + " " + p.getCourseCode() + " " + p.getRequiredSkills()).toLowerCase();
                        if (!blob.contains(qq)) continue;
                    }
                    filtered.add(p);
                }
                Set<String> depts = new LinkedHashSet<>();
                for (Position p : all) depts.add(p.getDepartment());
                req.setAttribute("positions", filtered);
                req.setAttribute("appliedIds", appliedIds);
                req.setAttribute("departments", new ArrayList<>(depts));
                req.setAttribute("filterQ", q != null ? q : "");
                req.setAttribute("filterDept", dept != null ? dept : "");
                req.getRequestDispatcher("/WEB-INF/jsp/ta/positions.jsp").forward(req, resp);
                break;
            }
            case "/position": {
                String id = req.getParameter("id");
                Position p = id == null ? null : positionService.getPositionById(id);
                if (p == null || !"APPROVED".equals(p.getStatus())) {
                    resp.sendRedirect(req.getContextPath() + "/ta/positions");
                    return;
                }
                req.setAttribute("sidebarActive", "positions");
                req.setAttribute("position", p);
                req.setAttribute("applied", applicationService.hasApplied(user.getId(), p.getId()));
                req.getRequestDispatcher("/WEB-INF/jsp/ta/position-detail.jsp").forward(req, resp);
                break;
            }
            case "/applications": {
                req.setAttribute("sidebarActive", "applications");
                String stRaw = req.getParameter("status");
                final String st = stRaw == null ? "ALL" : stRaw;
                List<Application> allA = applicationService.getApplicationsByUser(user.getId());
                List<Application> show = allA;
                if (!"ALL".equals(st)) {
                    show = allA.stream().filter(a -> st.equals(a.getStatus())).collect(Collectors.toList());
                }
                req.setAttribute("applications", show);
                req.setAttribute("filterStatus", st);
                req.getRequestDispatcher("/WEB-INF/jsp/ta/applications.jsp").forward(req, resp);
                break;
            }
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebAuth.requireRole(req, resp, userService, "TA")) return;
        req.setCharacterEncoding("UTF-8");
        User user = WebAuth.getUser(req, userService);
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/profile")) {
            String action = req.getParameter("formAction");
            User existing = userService.getUserById(user.getId());
            if (existing == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if ("password".equals(action)) {
                String oldPwd = req.getParameter("oldPassword");
                String newPwd = req.getParameter("newPassword");
                String confirm = req.getParameter("confirmPassword");
                if (newPwd == null || !newPwd.equals(confirm)) {
                    req.getSession().setAttribute("flashError", "Passwords do not match");
                } else if (userService.changePassword(user.getId(), oldPwd != null ? oldPwd : "", newPwd)) {
                    req.getSession().setAttribute("flashMsg", "Password changed");
                } else {
                    req.getSession().setAttribute("flashError", "Current password incorrect");
                }
                resp.sendRedirect(req.getContextPath() + "/ta/profile");
                return;
            }
            existing.setName(trim(req.getParameter("name")));
            existing.setPhone(trim(req.getParameter("phone")));
            existing.setProgramme(trim(req.getParameter("programme")));
            existing.setYearOfStudy(trim(req.getParameter("yearOfStudy")));
            existing.setUniversity(trim(req.getParameter("university")));
            existing.setGpa(trim(req.getParameter("gpa")));
            String skills = trim(req.getParameter("skills"));
            if (!skills.isEmpty()) {
                String[] parts = skills.split(",");
                StringBuilder sb = new StringBuilder();
                for (String p : parts) {
                    String t = p.trim();
                    if (!t.isEmpty()) {
                        if (sb.length() > 0) sb.append(';');
                        sb.append(t);
                    }
                }
                existing.setSkills(sb.toString());
            } else {
                existing.setSkills("");
            }
            existing.setExperience(trim(req.getParameter("experience")));
            userService.updateUser(existing);
            req.getSession().setAttribute("flashMsg", "Profile updated");
            resp.sendRedirect(req.getContextPath() + "/ta/profile");
            return;
        }

        if (path.equals("/apply")) {
            String positionId = trim(req.getParameter("positionId"));
            Position p = positionId.isEmpty() ? null : positionService.getPositionById(positionId);
            if (p == null || !"APPROVED".equals(p.getStatus())) {
                req.getSession().setAttribute("flashError", "Invalid position");
                resp.sendRedirect(req.getContextPath() + "/ta/positions");
                return;
            }
            if (applicationService.hasApplied(user.getId(), p.getId())) {
                req.getSession().setAttribute("flashError", "Already applied");
                resp.sendRedirect(req.getContextPath() + "/ta/position?id=" + p.getId());
                return;
            }
            String id = "APP" + String.format("%03d", applicationService.generateId());
            String date = LocalDate.now().toString();
            String title = p.getCourseName() + " TA";
            Application app = new Application(id, user.getId(), user.getName(), p.getId(), title,
                    p.getCourseCode(), date, "PENDING", "Under review");
            applicationService.createApplication(app);
            req.getSession().setAttribute("flashMsg", "Application submitted");
            resp.sendRedirect(req.getContextPath() + "/ta/applications");
            return;
        }

        resp.sendError(404);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
