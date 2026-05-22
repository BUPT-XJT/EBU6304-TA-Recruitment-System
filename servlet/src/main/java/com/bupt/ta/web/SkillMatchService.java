package com.bupt.ta.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rule-based skill matching (no external AI). Compares semicolon-separated
 * {@link User#getSkills()} against {@link Position#getRequiredSkills()}.
 */
public final class SkillMatchService {

    private SkillMatchService() {}

    public static SkillMatchResult match(User ta, Position position) {
        if (ta == null || position == null) {
            return emptyResult("Unable to match: missing profile or position");
        }
        return match(ta.getSkills(), position.getRequiredSkills());
    }

    public static SkillMatchResult match(String applicantSkills, String requiredSkills) {
        List<String> required = parseSkills(requiredSkills);
        List<String> applicant = parseSkills(applicantSkills);

        if (required.isEmpty()) {
            return new SkillMatchResult(100, List.of(), List.of(),
                    "No specific skills required for this role");
        }

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String req : required) {
            if (anyApplicantSkillMatches(req, applicant)) {
                matched.add(req);
            } else {
                missing.add(req);
            }
        }

        int score = (int) Math.round((matched.size() * 100.0) / required.size());
        String summary = buildSummary(score, matched, missing);
        return new SkillMatchResult(score, matched, missing, summary);
    }

    private static boolean anyApplicantSkillMatches(String required, List<String> applicant) {
        String reqNorm = normalize(required);
        if (reqNorm.isEmpty()) return false;
        for (String skill : applicant) {
            String appNorm = normalize(skill);
            if (appNorm.isEmpty()) continue;
            if (appNorm.contains(reqNorm) || reqNorm.contains(appNorm)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> parseSkills(String raw) {
        List<String> list = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) return list;
        for (String part : raw.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    private static String normalize(String skill) {
        return skill == null ? "" : skill.trim().toLowerCase(Locale.ROOT);
    }

    private static String buildSummary(int score, List<String> matched, List<String> missing) {
        StringBuilder sb = new StringBuilder();
        sb.append(score).append("% match");
        if (!matched.isEmpty()) {
            sb.append(" — matched: ").append(String.join(", ", matched));
        }
        if (!missing.isEmpty()) {
            sb.append("; missing: ").append(String.join(", ", missing));
        }
        return sb.toString();
    }

    private static SkillMatchResult emptyResult(String summary) {
        return new SkillMatchResult(0, List.of(), List.of(), summary);
    }
}
