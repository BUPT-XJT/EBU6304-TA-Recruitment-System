package com.bupt.ta.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Redirects locked legacy TA HTML paths to deadline-enabled pages. */
public class TaLegacyPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        String qs = req.getQueryString();
        String suffix = qs != null && !qs.isEmpty() ? "?" + qs : "";
        if (uri.endsWith("/ta/dashboard.html")) {
            resp.sendRedirect("/ta/dash.html" + suffix);
            return;
        }
        if (uri.endsWith("/ta/positions.html")) {
            resp.sendRedirect("/ta/browse.html" + suffix);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
