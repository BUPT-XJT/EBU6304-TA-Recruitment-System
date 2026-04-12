package com.bupt.ta.web.servlet;

import com.bupt.ta.web.User;
import com.bupt.ta.web.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = trim(req.getParameter("name"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String programme = trim(req.getParameter("programme"));

        if (password == null || !password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        String id = "TA" + String.format("%03d", userService.generateTAId());
        User user = new User(id, name, password, "TA", email, phone, programme, "", "", "", "", "", "");
        if (!userService.register(user)) {
            req.setAttribute("error", "Email already exists");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("newUserId", id);
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
