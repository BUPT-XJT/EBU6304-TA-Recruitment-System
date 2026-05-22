package com.bupt.ta.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recommends open TA positions for a user, ranked by {@link SkillMatchService} score.
 */
public class RecommendationService {

    private final PositionService positions;
    private final ApplicationService applications;

    public RecommendationService(PositionService positions, ApplicationService applications) {
        this.positions = positions;
        this.applications = applications;
    }

    public List<Map<String, Object>> recommendForTa(User ta, int limit) {
        if (ta == null || limit <= 0) return Collections.emptyList();

        List<ScoredPosition> scored = new ArrayList<>();
        for (Position p : positions.getOpenPositions()) {
            SkillMatchResult match = SkillMatchService.match(ta, p);
            boolean applied = applications.hasApplied(ta.getId(), p.getId());
            scored.add(new ScoredPosition(p, match, applied));
        }

        scored.sort(Comparator
                .comparing((ScoredPosition s) -> s.applied)
                .thenComparing((ScoredPosition s) -> -s.match.getMatchScore()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ScoredPosition sp : scored) {
            if (result.size() >= limit) break;
            result.add(toRecommendationMap(sp));
        }
        return result;
    }

    private Map<String, Object> toRecommendationMap(ScoredPosition sp) {
        Position p = sp.position;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("courseName", p.getCourseName());
        m.put("courseCode", p.getCourseCode());
        m.put("department", p.getDepartment());
        m.put("positionTitle", ApplicationRules.positionTitle(p));
        m.put("deadline", p.getDeadline());
        m.put("hoursPerWeek", p.getHoursPerWeek());
        m.put("requiredSkills", p.getRequiredSkills());
        m.put("alreadyApplied", sp.applied);
        m.put("skillMatch", JsonUtil.skillMatchToMap(sp.match));
        String urgency = PositionRules.deadlineUrgency(p);
        m.put("deadlineUrgency", urgency);
        m.put("deadlineLabel", PositionRules.deadlineLabel(urgency));
        m.put("daysUntilDeadline", PositionRules.daysUntilDeadline(p.getDeadline()));
        m.put("openForApplication", PositionRules.isOpenForTa(p));
        return m;
    }

    private static final class ScoredPosition {
        final Position position;
        final SkillMatchResult match;
        final boolean applied;

        ScoredPosition(Position position, SkillMatchResult match, boolean applied) {
            this.position = position;
            this.match = match;
            this.applied = applied;
        }
    }
}
