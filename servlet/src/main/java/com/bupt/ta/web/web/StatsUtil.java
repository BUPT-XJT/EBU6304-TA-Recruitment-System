package com.bupt.ta.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StatsUtil {
    private StatsUtil() {}

    public static Map<String, Object> compute(UserService userService,
                                                PositionService positionService,
                                                ApplicationService applicationService) {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<Position> allPos = positionService.getAllPositions();
        List<Application> allApps = applicationService.getAllApplications();
        List<User> allUsers = userService.getAllUsers();

        stats.put("totalPositions", allPos.size());
        int approved = 0, pending = 0;
        for (Position p : allPos) {
            if ("APPROVED".equals(p.getStatus())) approved++;
            if ("PENDING".equals(p.getStatus())) pending++;
        }
        stats.put("approvedPositions", approved);
        stats.put("pendingPositions", pending);
        stats.put("totalApplications", allApps.size());

        int appPending = 0, appPassed = 0, appFailed = 0;
        for (Application a : allApps) {
            if ("PENDING".equals(a.getStatus())) appPending++;
            if ("PASSED".equals(a.getStatus())) appPassed++;
            if ("FAILED".equals(a.getStatus())) appFailed++;
        }
        stats.put("pendingApplications", appPending);
        stats.put("passedApplications", appPassed);
        stats.put("failedApplications", appFailed);

        int taCount = 0, moCount = 0;
        for (User u : allUsers) {
            if ("TA".equals(u.getRole())) taCount++;
            if ("MO".equals(u.getRole())) moCount++;
        }
        stats.put("totalTAs", taCount);
        stats.put("totalMOs", moCount);
        stats.put("totalUsers", allUsers.size());
        return stats;
    }
}
