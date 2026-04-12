package com.bupt.ta.web.servlet;

import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = WebAuth.getUser(req, userService);
        if (u != null) {
            resp.sendRedirect(req.getContextPath() + WebAuth.dashboardPath(u));
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        User user = userService.login(userId != null ? userId.trim() : "", password != null ? password : "");
        if (user == null) {
            req.setAttribute("error", "Invalid credentials");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        resp.sendRedirect(req.getContextPath() + WebAuth.dashboardPath(user));
    }
}
