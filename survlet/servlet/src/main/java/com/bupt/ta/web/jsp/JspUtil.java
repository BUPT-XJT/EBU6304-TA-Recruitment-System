package com.bupt.ta.web.jsp;

public final class JspUtil {
    private JspUtil() {}

    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public static String badgeClass(String status) {
        if (status == null) return "badge-info";
        switch (status) {
            case "APPROVED":
            case "PASSED":
                return "badge-success";
            case "PENDING":
                return "badge-warning";
            case "REJECTED":
            case "FAILED":
                return "badge-danger";
            case "CLOSED":
                return "badge-gray";
            default:
                return "badge-info";
        }
    }

    public static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
    }

    public static String roleBadgeClass(String role) {
        if ("TA".equals(role)) return "badge-info";
        if ("MO".equals(role)) return "badge-purple";
        if ("ADMIN".equals(role)) return "badge-warning";
        return "badge-gray";
    }
}
