package com.bupt.ta.ui.component;

import com.bupt.ta.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                FontMetrics fm = g.getFontMetrics(getFont());
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                int x = (getWidth() - textW) / 2;
                int y = (getHeight() + textH) / 2 - 2;
                g.setFont(getFont());
                g.setColor(getForeground());
                g.drawString(getText(), x, y);
            }
        };
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(Theme.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 36));

        Color hoverBg = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    public static JButton createSmallButton(String text, Color bg, Color fg) {
        JButton btn = createButton(text, bg, fg);
        btn.setFont(Theme.FONT_SMALL_BOLD);
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 12, 28));
        return btn;
    }

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(Theme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRAY_200),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(Theme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRAY_200),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        return field;
    }

    public static JTextArea createTextArea(String placeholder, int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setFont(Theme.FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRAY_200),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        area.setToolTipText(placeholder);
        return area;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRAY_200),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        return card;
    }

    public static JPanel createStatCard(String label, String value, Color valueColor) {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.GRAY_500);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(valueColor);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(val);
        return card;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setFont(Theme.FONT_SMALL_BOLD);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        return badge;
    }

    public static JLabel createTag(String text) {
        JLabel tag = new JLabel(text);
        tag.setOpaque(true);
        tag.setBackground(Theme.GRAY_100);
        tag.setForeground(Theme.GRAY_600);
        tag.setFont(Theme.FONT_SMALL);
        tag.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return tag;
    }

    public static void styleTable(JTable table) {
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.GRAY_800);
        table.setBackground(Color.WHITE);
        table.setBorder(BorderFactory.createLineBorder(Theme.GRAY_200));

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_BODY_BOLD);
        header.setBackground(Theme.GRAY_50);
        header.setForeground(Theme.GRAY_700);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GRAY_200));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.GRAY_50);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static JPanel createFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_BODY_BOLD);
        label.setForeground(Theme.GRAY_700);
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }
}
