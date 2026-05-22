package com.bupt.ta.web.api;

import com.bupt.ta.web.DeadlineReminderService;
import com.bupt.ta.web.JsonUtil;
import com.bupt.ta.web.User;
import com.bupt.ta.web.WebAuth;
import com.bupt.ta.web.WebAuth.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DeadlineReminderHandler extends BaseApiHandler {

    private final DeadlineReminderService reminders;

    public DeadlineReminderHandler(ApiContext ctx) {
        super(ctx);
        this.reminders = new DeadlineReminderService(ctx.positions, ctx.applications);
    }

    public boolean handleGet(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!path.equals("/deadline-reminders")) return false;
        SessionUser session = WebAuth.requireRole(req, resp, "TA");
        if (session == null) return true;
        User ta = ctx.users.getUserById(session.userId);
        if (ta == null) {
            sendError(resp, 404, "User not found");
            return true;
        }
        Map<String, List<Map<String, Object>>> data = reminders.getRemindersForTa(ta);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("closingSoon", data.get("closingSoon"));
        payload.put("expired", data.get("expired"));
        sendJson(resp, JsonUtil.toJson(payload));
        return true;
    }
}
