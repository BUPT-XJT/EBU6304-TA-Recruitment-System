package com.bupt.ta.web.api;

import com.bupt.ta.web.JsonUtil;
import com.bupt.ta.web.RecommendationService;
import com.bupt.ta.web.User;
import com.bupt.ta.web.WebAuth;
import com.bupt.ta.web.WebAuth.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class RecommendationHandler extends BaseApiHandler {

    private final RecommendationService recommendations;

    public RecommendationHandler(ApiContext ctx) {
        super(ctx);
        this.recommendations = new RecommendationService(ctx.positions, ctx.applications);
    }

    public boolean handleGet(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!path.equals("/recommendations")) return false;
        handleGetRecommendations(req, resp);
        return true;
    }

    private void handleGetRecommendations(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUser session = WebAuth.requireRole(req, resp, "TA");
        if (session == null) return;

        User ta = ctx.users.getUserById(session.userId);
        if (ta == null) {
            sendError(resp, 404, "User not found");
            return;
        }

        int limit = parseInt(req.getParameter("limit"), 3);
        if (limit > 20) limit = 20;

        sendJson(resp, JsonUtil.toJsonArray(recommendations.recommendForTa(ta, limit)));
    }
}
