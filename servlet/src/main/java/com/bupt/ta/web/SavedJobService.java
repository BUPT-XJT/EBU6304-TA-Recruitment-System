package com.bupt.ta.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SavedJobService {
    private static final String FILE = "data/saved_jobs.txt";

    public List<SavedJob> getAll() {
        List<SavedJob> list = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            SavedJob job = SavedJob.fromLine(line);
            if (job != null) list.add(job);
        }
        return list;
    }

    public List<SavedJob> getByUser(String userId) {
        List<SavedJob> result = new ArrayList<>();
        for (SavedJob job : getAll()) {
            if (userId.equals(job.getUserId())) result.add(job);
        }
        return result;
    }

    public boolean isSaved(String userId, String positionId) {
        for (SavedJob job : getByUser(userId)) {
            if (positionId.equals(job.getPositionId())) return true;
        }
        return false;
    }

    /** @return false if already saved (duplicate) */
    public boolean save(String userId, String positionId) {
        if (isSaved(userId, positionId)) return false;
        List<String> lines = FileUtil.read(FILE);
        lines.add(new SavedJob(userId, positionId, LocalDate.now().toString()).toLine());
        FileUtil.write(FILE, lines);
        return true;
    }

    public boolean unsave(String userId, String positionId) {
        List<SavedJob> all = getAll();
        List<String> lines = new ArrayList<>();
        boolean removed = false;
        for (SavedJob job : all) {
            if (userId.equals(job.getUserId()) && positionId.equals(job.getPositionId())) {
                removed = true;
                continue;
            }
            lines.add(job.toLine());
        }
        if (removed) FileUtil.write(FILE, lines);
        return removed;
    }
}
