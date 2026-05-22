package com.bupt.ta.web;

/** TA favourite: userId + positionId stored in data/saved_jobs.txt */
public class SavedJob {
    private final String userId;
    private final String positionId;
    private final String savedAt;

    public SavedJob(String userId, String positionId, String savedAt) {
        this.userId = userId;
        this.positionId = positionId;
        this.savedAt = savedAt != null ? savedAt : "";
    }

    public String getUserId() { return userId; }
    public String getPositionId() { return positionId; }
    public String getSavedAt() { return savedAt; }

    public String toLine() {
        return String.join("|", userId, positionId, savedAt);
    }

    public static SavedJob fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] s = line.split("\\|", -1);
        if (s.length < 2) return null;
        return new SavedJob(s[0], s[1], s.length > 2 ? s[2] : "");
    }
}
