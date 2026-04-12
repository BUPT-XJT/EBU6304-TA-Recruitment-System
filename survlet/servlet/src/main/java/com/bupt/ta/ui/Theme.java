package com.bupt.ta.ui;

import java.awt.*;

public class Theme {
    /** Rounded corners for controls (px). */
    public static final int CORNER_RADIUS = 16;
    /** Larger radius for cards / login panels / job cards. */
    public static final int CORNER_RADIUS_LARGE = 32;

    /** Main window & content area — soft cool gray (less flat than plain gray-50). */
    public static final Color WINDOW_BG = new Color(241, 245, 250);

    /** Subtle tint for card footers / highlighted strips. */
    public static final Color CARD_FOOTER_BG = new Color(239, 246, 255);
    public static final Color CARD_FOOTER_BORDER = new Color(219, 234, 254);

    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color SECONDARY = new Color(124, 58, 237);
    public static final Color SUCCESS = new Color(5, 150, 105);
    public static final Color SUCCESS_LIGHT = new Color(209, 250, 229);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);

    public static final Color GRAY_50 = new Color(249, 250, 251);
    public static final Color GRAY_100 = new Color(243, 244, 246);
    public static final Color GRAY_200 = new Color(229, 231, 235);
    public static final Color GRAY_300 = new Color(209, 213, 219);
    public static final Color GRAY_400 = new Color(156, 163, 175);
    public static final Color GRAY_500 = new Color(107, 114, 128);
    public static final Color GRAY_600 = new Color(75, 85, 99);
    public static final Color GRAY_700 = new Color(55, 65, 81);
    public static final Color GRAY_800 = new Color(31, 41, 55);
    public static final Color GRAY_900 = new Color(17, 24, 39);

    /** Softer slate sidebar (less harsh than pure gray-900). */
    public static final Color SIDEBAR_TA = new Color(30, 41, 59);
    /** Selected row pill tint (primary @ ~28% alpha). */
    public static final Color SIDEBAR_ACTIVE_PILL = new Color(37, 99, 235, 72);
    public static final Color SIDEBAR_GLYPH_BG = new Color(255, 255, 255, 38);
    public static final Color SIDEBAR_GLYPH_BG_ACTIVE = new Color(255, 255, 255, 95);
    public static final Color SIDEBAR_MO = new Color(26, 16, 64);
    public static final Color SIDEBAR_ADMIN = new Color(69, 26, 3);

    /** Slightly lighter top stop for sidebar vertical gradients. */
    public static Color sidebarGradientTop(Color base) {
        return new Color(
                clamp255(base.getRed() + 22),
                clamp255(base.getGreen() + 26),
                clamp255(base.getBlue() + 34));
    }

    /** Slightly deeper bottom stop for sidebar vertical gradients. */
    public static Color sidebarGradientBottom(Color base) {
        return new Color(
                clamp255(base.getRed() - 28),
                clamp255(base.getGreen() - 22),
                clamp255(base.getBlue() - 14));
    }

    private static int clamp255(int v) {
        return Math.max(0, Math.min(255, v));
    }

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_H3 = new Font("Segoe UI", Font.BOLD, 16);
    /** Slightly larger field labels (e.g. Settings password form). */
    public static final Font FONT_SETTINGS_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_ACTIVE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 10);
}
