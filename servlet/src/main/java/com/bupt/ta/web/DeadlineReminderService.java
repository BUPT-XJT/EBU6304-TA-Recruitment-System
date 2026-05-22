package com.bupt.ta.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builds TA deadline reminder lists from approved positions. */
public class DeadlineReminderService {

    private final PositionService positions;
    private final ApplicationService applications;

    public DeadlineReminderService(PositionService positions, ApplicationService applications) {
        this.positions = positions;
        this.applications = applications;
    }

    public Map<String, List<Map<String, Object>>> getRemindersForTa(User ta) {
        List<Map<String, Object>> closingSoon = new ArrayList<>();
        List<Map<String, Object>> expired = new ArrayList<>();

        for (Position p : positions.getApprovedPositions()) {
            String urgency = PositionRules.deadlineUrgency(p);
            Map<String, Object> row = toReminderRow(p, ta, urgency);
            if (PositionRules.URGENCY_EXPIRED.equals(urgency)
                    || PositionRules.URGENCY_CLOSED.equals(urgency)) {
                expired.add(row);
            } else if (PositionRules.URGENCY_CLOSING_SOON.equals(urgency)
                    || PositionRules.URGENCY_DEADLINE_TODAY.equals(urgency)) {
                closingSoon.add(row);
            }
        }

        closingSoon.sort(Comparator.comparingLong(m -> (Long) m.get("daysUntilDeadline")));
        expired.sort(Comparator
                .comparingLong((Map<String, Object> m) -> (Long) m.get("daysUntilDeadline"))
                .reversed());

        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("closingSoon", closingSoon);
        result.put("expired", expired);
        return result;
    }

    private Map<String, Object> toReminderRow(Position p, User ta, String urgency) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("courseName", p.getCourseName());
        m.put("courseCode", p.getCourseCode());
        m.put("department", p.getDepartment());
        m.put("deadline", p.getDeadline());
        m.put("positionTitle", ApplicationRules.positionTitle(p));
        m.put("deadlineUrgency", urgency);
        m.put("deadlineLabel", PositionRules.deadlineLabel(urgency));
        m.put("daysUntilDeadline", PositionRules.daysUntilDeadline(p.getDeadline()));
        m.put("openForApplication", PositionRules.isOpenForTa(p));
        if (ta != null) {
            m.put("alreadyApplied", applications.hasApplied(ta.getId(), p.getId()));
            m.put("skillMatch", JsonUtil.skillMatchToMap(SkillMatchService.match(ta, p)));
        }
        return m;
    }
}
