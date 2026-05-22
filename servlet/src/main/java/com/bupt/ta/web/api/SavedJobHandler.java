package com.bupt.ta.web.api;

import com.bupt.ta.web.ApplicationRules;
import com.bupt.ta.web.JsonUtil;
import com.bupt.ta.web.Position;
import com.bupt.ta.web.PositionRules;
import com.bupt.ta.web.SavedJob;
import com.bupt.ta.web.User;
import com.bupt.ta.web.WebAuth;
import com.bupt.ta.web.WebAuth.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SavedJobHandler extends BaseApiHandler {

    public SavedJobHandler(ApiContext ctx) {
        super(ctx);
    }

    public boolean handleGet(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!path.equals("/saved-jobs")) return false;
        handleList(req, resp);
        return true;
    }

    public boolean handlePost(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!path.equals("/saved-jobs")) return false;
        handleSave(req, resp);
        return true;
    }

    public boolean handleDelete(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!path.startsWith("/saved-jobs/")) return false;
        String positionId = path.substring("/saved-jobs/".length());
        if (positionId.isEmpty() || positionId.contains("/")) return false;
        handleUnsave(req, resp, positionId);
        return true;
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUser session = WebAuth.requireRole(req, resp, "TA");
        if (session == null) return;

        User ta = ctx.users.getUserById(session.userId);
        if (ta == null) {
            sendError(resp, 404, "User not found");
            return;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (SavedJob saved : ctx.savedJobs.getByUser(session.userId)) {
            Position p = ctx.positions.getPositionById(saved.getPositionId());
            if (p == null) continue;
            Map<String, Object> m = JsonUtil.positionToMap(p);
            m.put("savedAt", saved.getSavedAt());
            m.put("positionTitle", ApplicationRules.positionTitle(p));
            m.put("openForApplication", PositionRules.isOpenForTa(p));
            m.put("alreadyApplied", ctx.applications.hasApplied(session.userId, p.getId()));
            attachSkillMatch(m, ta, p);
            list.add(m);
        }
        sendJson(resp, JsonUtil.toJsonArray(list));
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUser session = WebAuth.requireRole(req, resp, "TA");
        if (session == null) return;

        String body = JsonUtil.readBody(req);
        String positionId = JsonUtil.getJsonString(body, "positionId");
        if (positionId.isEmpty()) {
            sendError(resp, 400, "positionId is required");
            return;
        }

        Position pos = ctx.positions.getPositionById(positionId);
        if (pos == null) {
            sendError(resp, 404, "Position not found");
            return;
        }

        if (!ctx.savedJobs.save(session.userId, positionId)) {
            sendError(resp, 409, "Job already saved");
            return;
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", true);
        r.put("positionId", positionId);
        sendJson(resp, JsonUtil.toJson(r));
    }

    private void handleUnsave(HttpServletRequest req, HttpServletResponse resp, String positionId)
            throws IOException {
        SessionUser session = WebAuth.requireRole(req, resp, "TA");
        if (session == null) return;

        boolean ok = ctx.savedJobs.unsave(session.userId, positionId);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", ok);
        if (!ok) r.put("message", "Saved job not found");
        sendJson(resp, JsonUtil.toJson(r));
    }
}
