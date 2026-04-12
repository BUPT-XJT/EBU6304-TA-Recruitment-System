package com.bupt.ta.web.servlet;

import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomeServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = WebAuth.getUser(req, userService);
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            resp.sendRedirect(req.getContextPath() + WebAuth.dashboardPath(u));
        }
    }
}
