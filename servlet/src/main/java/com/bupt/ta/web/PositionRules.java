package com.bupt.ta.web;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Position lifecycle: PENDING → APPROVED|REJECTED; APPROVED → CLOSED. */
public final class PositionRules {
    public static final int CLOSING_SOON_DAYS = 3;

    public static final String URGENCY_OPEN = "OPEN";
    public static final String URGENCY_CLOSING_SOON = "CLOSING_SOON";
    public static final String URGENCY_DEADLINE_TODAY = "DEADLINE_TODAY";
    public static final String URGENCY_EXPIRED = "EXPIRED";
    public static final String URGENCY_CLOSED = "CLOSED";

    private static final Set<String> STATUSES = new HashSet<>(
            Arrays.asList("PENDING", "APPROVED", "REJECTED", "CLOSED"));

    private PositionRules() {}

    public static boolean isValidStatus(String status) {
        return status != null && STATUSES.contains(status);
    }

    public static LocalDate parseDeadline(String deadline) {
        if (deadline == null || deadline.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(deadline.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isPastDeadline(String deadline) {
        LocalDate d = parseDeadline(deadline);
        if (d == null) return false;
        return LocalDate.now().isAfter(d);
    }

    /** Days from today until deadline (negative if already passed). */
    public static long daysUntilDeadline(String deadline) {
        LocalDate d = parseDeadline(deadline);
        if (d == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), d);
    }

    /**
     * Deadline urgency for TA reminders.
     * EXPIRED = past deadline; DEADLINE_TODAY; CLOSING_SOON = within {@link #CLOSING_SOON_DAYS} days.
     */
    public static String deadlineUrgency(Position pos) {
        if (pos == null) return URGENCY_OPEN;
        if ("CLOSED".equals(pos.getStatus()) || "REJECTED".equals(pos.getStatus())) {
            return URGENCY_CLOSED;
        }
        if (!"APPROVED".equals(pos.getStatus())) return URGENCY_OPEN;
        LocalDate d = parseDeadline(pos.getDeadline());
        if (d == null) return URGENCY_OPEN;
        LocalDate today = LocalDate.now();
        if (d.isBefore(today)) return URGENCY_EXPIRED;
        if (d.equals(today)) return URGENCY_DEADLINE_TODAY;
        if (!d.isAfter(today.plusDays(CLOSING_SOON_DAYS))) return URGENCY_CLOSING_SOON;
        return URGENCY_OPEN;
    }

    public static String deadlineLabel(String urgency) {
        if (urgency == null) return "";
        switch (urgency) {
            case URGENCY_EXPIRED: return "Expired";
            case URGENCY_DEADLINE_TODAY: return "Deadline Today";
            case URGENCY_CLOSING_SOON: return "Closing Soon";
            case URGENCY_CLOSED: return "Closed";
            default: return "";
        }
    }

    /** TA may browse and apply: approved, not closed/rejected, not past deadline. */
    public static boolean isOpenForTa(Position pos) {
        return pos != null
                && "APPROVED".equals(pos.getStatus())
                && !isPastDeadline(pos.getDeadline());
    }

    /**
     * @return null if allowed, otherwise an error message for the client
     */
    public static String validateTransition(String role, String userId, Position pos, String newStatus) {
        if (pos == null) return "Position not found";
        if (!isValidStatus(newStatus)) return "Invalid status";

        String from = pos.getStatus();
        if (from.equals(newStatus)) return null;

        if ("REJECTED".equals(from) || "CLOSED".equals(from)) {
            return "Position status cannot be changed from " + from;
        }

        if ("ADMIN".equals(role)) {
            if ("PENDING".equals(from) && ("APPROVED".equals(newStatus) || "REJECTED".equals(newStatus))) {
                return null;
            }
            if ("APPROVED".equals(from) && "CLOSED".equals(newStatus)) {
                return null;
            }
            return "Admin cannot change status from " + from + " to " + newStatus;
        }

        if ("MO".equals(role)) {
            if (!userId.equals(pos.getMoId())) {
                return "Cannot manage another module owner's position";
            }
            if ("APPROVED".equals(from) && "CLOSED".equals(newStatus)) {
                return null;
            }
            return "Cannot change status from " + from + " to " + newStatus;
        }

        return "Insufficient permissions";
    }
}
