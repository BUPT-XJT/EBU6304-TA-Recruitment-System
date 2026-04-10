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
    private final List<JLabel> menuItems = new ArrayList<>();
    private final Color bgColor;
    private final Color accentColor;
    private final String brandText;
    private Consumer<String> onSelect;
    private String activeItem = "";

    public Sidebar(Color bgColor, Color accentColor, String brandText) {
        this.bgColor = bgColor;
        this.accentColor = accentColor;
        this.brandText = brandText;
        setBackground(bgColor);
        setPreferredSize(new Dimension(220, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setMaximumSize(new Dimension(220, 50));

        JLabel icon = new JLabel(brandText.substring(0, 2).toUpperCase());
        icon.setOpaque(true);
        icon.setBackground(accentColor);
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(32, 32));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JLabel brandLabel = new JLabel("TA Recruit");
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        brandPanel.add(icon);
        brandPanel.add(brandLabel);
        add(brandPanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 25));
        sep.setMaximumSize(new Dimension(220, 1));
        add(Box.createVerticalStrut(12));
        add(sep);
        add(Box.createVerticalStrut(8));
    }

    public void setOnSelect(Consumer<String> onSelect) {
        this.onSelect = onSelect;
    }

    public void addSection(String title) {
        add(Box.createVerticalStrut(8));
        JLabel label = new JLabel("  " + title.toUpperCase());
        label.setForeground(new Color(255, 255, 255, 90));
        label.setFont(Theme.FONT_SECTION);
        label.setMaximumSize(new Dimension(220, 20));
        label.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 0));
        add(label);
    }

    public void addMenuItem(String icon, String text) {
        JLabel item = new JLabel("  " + icon + "  " + text);
        item.setForeground(new Color(255, 255, 255, 153));
        item.setFont(Theme.FONT_SIDEBAR);
        item.setOpaque(true);
        item.setBackground(bgColor);
        item.setMaximumSize(new Dimension(220, 36));
        item.setPreferredSize(new Dimension(220, 36));
        item.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(activeItem)) {
                    item.setBackground(new Color(255, 255, 255, 12));
                    item.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!text.equals(activeItem)) {
                    item.setBackground(bgColor);
                    item.setForeground(new Color(255, 255, 255, 153));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActive(text);
                if (onSelect != null) onSelect.accept(text);
            }
        });

        menuItems.add(item);
        add(item);
    }

    public void setActive(String text) {
        this.activeItem = text;
        for (JLabel item : menuItems) {
            String itemText = item.getText().trim();
            String cleanName = itemText.length() > 4 ? itemText.substring(4).trim() : itemText;
            if (cleanName.equals(text)) {
                item.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
                item.setForeground(new Color(96, 165, 250));
                item.setFont(Theme.FONT_SIDEBAR_ACTIVE);
                item.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(96, 165, 250)),
                        BorderFactory.createEmptyBorder(0, 13, 0, 0)
                ));
            } else {
                item.setBackground(bgColor);
                item.setForeground(new Color(255, 255, 255, 153));
                item.setFont(Theme.FONT_SIDEBAR);
                item.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            }
        }
    }

    public void addUserSection(String name, String initials) {
        add(Box.createVerticalGlue());
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 25));
        sep.setMaximumSize(new Dimension(220, 1));
        add(sep);
        add(Box.createVerticalStrut(8));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(220, 40));

        JLabel avatar = new JLabel(initials);
        avatar.setOpaque(true);
        avatar.setBackground(Theme.PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30, 30));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(new Color(255, 255, 255, 200));
        nameLabel.setFont(Theme.FONT_BODY);

        userPanel.add(avatar);
        userPanel.add(nameLabel);
        add(userPanel);
    }
}
