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
import java.util.List;
import java.util.stream.Collectors;

public class MoServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebAuth.requireRole(req, resp, userService, "MO")) return;

        User user = WebAuth.getUser(req, userService);
        String path = req.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            return;
        }

        req.setAttribute("currentUser", user);
        req.setAttribute("sidebarRole", "MO");

        List<Position> myPos = positionService.getPositionsByMo(user.getId());
        List<Application> allApps = applicationService.getAllApplications();

        switch (path) {
            case "/dashboard":
                req.setAttribute("sidebarActive", "dashboard");
                req.setAttribute("positions", myPos);
                req.setAttribute("allApplications", allApps);
                req.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(req, resp);
                break;
            case "/publish":
                req.setAttribute("sidebarActive", "publish");
                req.getRequestDispatcher("/WEB-INF/jsp/mo/publish.jsp").forward(req, resp);
                break;
            case "/review": {
                req.setAttribute("sidebarActive", "review");
                String posId = req.getParameter("positionId");
                List<Position> approvedMine = myPos.stream()
                        .filter(p -> "APPROVED".equals(p.getStatus()))
                        .collect(Collectors.toList());
                req.setAttribute("myApprovedPositions", approvedMine);
                List<Application> forPos = null;
                if (posId != null && !posId.isEmpty()) {
                    forPos = allApps.stream()
                            .filter(a -> posId.equals(a.getPositionId()))
                            .collect(Collectors.toList());
                }
                req.setAttribute("selectedPositionId", posId != null ? posId : "");
                req.setAttribute("reviewApplications", forPos);
                req.getRequestDispatcher("/WEB-INF/jsp/mo/review.jsp").forward(req, resp);
                break;
            }
            case "/offers": {
                req.setAttribute("sidebarActive", "offers");
                String posId = req.getParameter("positionId");
                List<Position> approvedMine = myPos.stream()
                        .filter(p -> "APPROVED".equals(p.getStatus()))
                        .collect(Collectors.toList());
                req.setAttribute("myApprovedPositions", approvedMine);
                Position selected = null;
                List<Application> passed = null;
                if (posId != null && !posId.isEmpty()) {
                    selected = positionService.getPositionById(posId);
                    if (selected != null && user.getId().equals(selected.getMoId())) {
                        passed = allApps.stream()
                                .filter(a -> posId.equals(a.getPositionId()) && "PASSED".equals(a.getStatus()))
                                .collect(Collectors.toList());
                    }
                }
                req.setAttribute("offerPosition", selected);
                req.setAttribute("offerApplications", passed);
                req.setAttribute("selectedOfferPositionId", posId != null ? posId : "");
                req.getRequestDispatcher("/WEB-INF/jsp/mo/offers.jsp").forward(req, resp);
                break;
            }
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebAuth.requireRole(req, resp, userService, "MO")) return;
        req.setCharacterEncoding("UTF-8");
        User user = WebAuth.getUser(req, userService);
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/publish")) {
            String courseName = trim(req.getParameter("courseName"));
            String courseCode = trim(req.getParameter("courseCode"));
            String department = trim(req.getParameter("department"));
            int numPositions = parseInt(req.getParameter("numPositions"), 1);
            int hoursPerWeek = parseInt(req.getParameter("hoursPerWeek"), 0);
            String payRate = trim(req.getParameter("payRate"));
            String deadline = trim(req.getParameter("deadline"));
            String requiredSkills = trim(req.getParameter("requiredSkills"));
            String duties = req.getParameter("duties") != null ? req.getParameter("duties").trim() : "";

            String id = "POS" + String.format("%03d", positionService.generateId());
            Position pos = new Position(id, user.getId(), user.getName(), courseName, courseCode, department,
                    numPositions, hoursPerWeek, payRate, deadline, requiredSkills, duties, "PENDING");
            positionService.addPosition(pos);
            req.getSession().setAttribute("flashMsg", "Position submitted for admin approval");
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            return;
        }

        if (path.equals("/review")) {
            String appId = trim(req.getParameter("applicationId"));
            String status = trim(req.getParameter("status"));
            String feedback = req.getParameter("feedback") != null ? req.getParameter("feedback").trim() : "";
            if ("PASSED".equals(status) && feedback.isEmpty()) feedback = "Accepted";
            if ("FAILED".equals(status) && feedback.isEmpty()) feedback = "Rejected";
            applicationService.updateApplicationStatus(appId, status, feedback);
            req.getSession().setAttribute("flashMsg", "Application updated");
            String posId = trim(req.getParameter("returnPositionId"));
            resp.sendRedirect(req.getContextPath() + "/mo/review?positionId=" + posId);
            return;
        }

        resp.sendError(404);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
