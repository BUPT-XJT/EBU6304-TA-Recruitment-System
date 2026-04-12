package com.bupt.ta.web.servlet;

import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public final class WebAuth {
    private WebAuth() {}

    public static User getUser(HttpServletRequest req, UserService userService) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object id = session.getAttribute("userId");
        if (id == null) return null;
        return userService.getUserById(String.valueOf(id));
    }

    public static boolean requireRole(HttpServletRequest req, HttpServletResponse resp,
                                      UserService userService, String role) throws IOException {
        User u = getUser(req, userService);
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (role != null && !role.equals(u.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    public static String dashboardPath(User u) {
        if (u == null) return "/login";
        switch (u.getRole()) {
            case "TA":
                return "/ta/dashboard";
            case "MO":
                return "/mo/dashboard";
            case "ADMIN":
                return "/admin/dashboard";
            default:
                return "/login";
        }
    }
}
