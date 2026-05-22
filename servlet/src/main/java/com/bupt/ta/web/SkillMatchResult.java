package com.bupt.ta.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Rule-based skill match outcome for a TA profile against a position. */
public final class SkillMatchResult {
    private final int matchScore;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String summary;

    public SkillMatchResult(int matchScore, List<String> matchedSkills,
                            List<String> missingSkills, String summary) {
        this.matchScore = Math.max(0, Math.min(100, matchScore));
        this.matchedSkills = Collections.unmodifiableList(new ArrayList<>(matchedSkills));
        this.missingSkills = Collections.unmodifiableList(new ArrayList<>(missingSkills));
        this.summary = summary != null ? summary : "";
    }

    public int getMatchScore() { return matchScore; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public String getSummary() { return summary; }
}
