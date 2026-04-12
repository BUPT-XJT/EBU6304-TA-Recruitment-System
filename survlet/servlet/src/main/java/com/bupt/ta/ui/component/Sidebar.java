package com.bupt.ta.ui.component;

import com.bupt.ta.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends JPanel {

    /**
     * Menu row: no missing-glyph Unicode — icon is a single ASCII letter in Segoe UI.
     * Rows are non-opaque so the parent sidebar gradient shows through.
     */
    private final class PaintedMenuRow extends JPanel {
        private final String glyph;
        private final String label;
        private final String menuKey;

        PaintedMenuRow(String glyph, String label, String menuKey) {
            this.glyph = glyph;
            this.label = label;
            this.menuKey = menuKey;
            setOpaque(false);
            setDoubleBuffered(true);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension d = new Dimension(228, 46);
            setPreferredSize(d);
            setMaximumSize(d);
            setMinimumSize(new Dimension(180, 46));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Sidebar.this.setActive(menuKey);
                    if (onSelect != null) {
                        onSelect.accept(menuKey);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            boolean active = menuKey.equals(activeItem);
            int leftInset = 12;
            int pillPadY = 6;
            int pillW = w - leftInset - 10;
            int pillH = h - 2 * pillPadY;
            if (active) {
                g2.setColor(Theme.SIDEBAR_ACTIVE_PILL);
                g2.fillRoundRect(leftInset, pillPadY, pillW, pillH, 14, 14);
            }

            int glyphBox = 24;
            int gbx = leftInset + 4;
            int gby = (h - glyphBox) / 2;
            g2.setColor(active ? Theme.SIDEBAR_GLYPH_BG_ACTIVE : Theme.SIDEBAR_GLYPH_BG);
            g2.fillRoundRect(gbx, gby, glyphBox, glyphBox, 9, 9);

            Font textFont = active ? Theme.FONT_SIDEBAR_ACTIVE : Theme.FONT_SIDEBAR;
            Font glyphFont = new Font("Segoe UI", Font.BOLD, 12);

            g2.setFont(glyphFont);
            FontMetrics fmGlyph = g2.getFontMetrics(glyphFont);
            int baseline = (h - fmGlyph.getHeight()) / 2 + fmGlyph.getAscent();
            g2.setColor(active ? new Color(30, 58, 138) : new Color(255, 255, 255, 235));
            int gw = fmGlyph.stringWidth(glyph);
            g2.drawString(glyph, gbx + (glyphBox - gw) / 2, baseline);

            Color textColor = active ? Color.WHITE : new Color(255, 255, 255, 185);
            g2.setFont(textFont);
            FontMetrics fmText = g2.getFontMetrics(textFont);
            int textX = gbx + glyphBox + 10;
            int maxW = w - textX - 10;
            String toDraw = label;
            if (fmText.stringWidth(toDraw) > maxW) {
                String ell = "...";
                while (toDraw.length() > 0 && fmText.stringWidth(toDraw + ell) > maxW) {
                    toDraw = toDraw.substring(0, toDraw.length() - 1);
                }
                toDraw = toDraw + ell;
            }
            g2.setColor(textColor);
            baseline = (h - fmText.getHeight()) / 2 + fmText.getAscent();
            g2.drawString(toDraw, textX, baseline);

            g2.dispose();
        }
    }

    private final List<PaintedMenuRow> menuRows = new ArrayList<>();
    private final Color accentColor;
    private final Color gradientTop;
    private final Color gradientBottom;
    private Consumer<String> onSelect;
    private String activeItem = "";

    public Sidebar(Color bgColor, Color accentColor, String brandText) {
        this.accentColor = accentColor;
        this.gradientTop = Theme.sidebarGradientTop(bgColor);
        this.gradientBottom = Theme.sidebarGradientBottom(bgColor);
        setOpaque(false);
        setPreferredSize(new Dimension(228, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setDoubleBuffered(true);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setMaximumSize(new Dimension(228, 50));

        final String brandLetters = brandText.substring(0, 2).toUpperCase();
        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Sidebar.this.accentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(brandLetters)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g.drawString(brandLetters, x, y);
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(32, 32));

        JLabel brandLabel = new JLabel("TA Recruit");
        brandLabel.setForeground(new Color(255, 255, 255, 245));
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        brandPanel.add(icon);
        brandPanel.add(brandLabel);
        add(brandPanel);

        add(Box.createVerticalStrut(20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.setPaint(new GradientPaint(0, 0, gradientTop, 0, h, gradientBottom));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(w - 8, 0, new Color(15, 23, 42, 45), w, 0, new Color(15, 23, 42, 0)));
        g2.fillRect(w - 8, 0, 8, h);
        g2.dispose();
    }

    public void setOnSelect(Consumer<String> onSelect) {
        this.onSelect = onSelect;
    }

    public void addSection(String title) {
        add(Box.createVerticalStrut(14));
        JLabel label = new JLabel("  " + title.toUpperCase());
        label.setForeground(new Color(255, 255, 255, 100));
        label.setFont(Theme.FONT_SECTION);
        label.setMaximumSize(new Dimension(228, 22));
        label.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(6));
    }

    public void addMenuItem(String icon, String text) {
        PaintedMenuRow row = new PaintedMenuRow(icon, text, text);
        menuRows.add(row);
        add(row);
        add(Box.createVerticalStrut(6));
    }

    public void setActive(String text) {
        this.activeItem = text != null ? text : "";
        for (PaintedMenuRow row : menuRows) {
            row.repaint();
        }
    }

    public void addUserSection(String name, String initials) {
        add(Box.createVerticalGlue());
        add(Box.createVerticalStrut(12));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(228, 44));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setLayout(new BorderLayout());
        JLabel avText = new JLabel(initials);
        avText.setForeground(Color.WHITE);
        avText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avText.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.add(avText, BorderLayout.CENTER);
        avatar.setPreferredSize(new Dimension(32, 32));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(new Color(255, 255, 255, 210));
        nameLabel.setFont(Theme.FONT_BODY);

        userPanel.add(avatar);
        userPanel.add(nameLabel);
        add(userPanel);
    }
}
