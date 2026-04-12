package com.bupt.ta.ui.component;

import com.bupt.ta.ui.Theme;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    private static final class RoundedOutlineBorder extends AbstractBorder {
        private final Color color;
        private final float thickness;
        private final int arc;
        /** When null, padding is derived from thickness (for multi-line fields). */
        private final Insets padding;

        RoundedOutlineBorder(Color color, float thickness, int arc) {
            this(color, thickness, arc, null);
        }

        RoundedOutlineBorder(Color color, float thickness, int arc, Insets padding) {
            this.color = color;
            this.thickness = thickness;
            this.arc = arc;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            float t = thickness / 2f;
            RoundRectangle2D rr = new RoundRectangle2D.Float(x + t, y + t, width - 2 * t, height - 2 * t, arc, arc);
            g2.draw(rr);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            if (padding != null) {
                return new Insets(padding.top, padding.left, padding.bottom, padding.right);
            }
            int p = (int) Math.ceil(thickness) + 8;
            int q = (int) Math.ceil(thickness) + 12;
            return new Insets(p, q, p, q);
        }
    }

    private static final Border FIELD_BORDER_DEFAULT = new RoundedOutlineBorder(Theme.GRAY_200, 1f, Theme.CORNER_RADIUS);
    private static final Border FIELD_BORDER_FOCUS = new RoundedOutlineBorder(Theme.PRIMARY, 2f, Theme.CORNER_RADIUS);

    /** Tighter vertical padding so single-line fields look balanced. */
    private static final Border FIELD_SINGLE_DEFAULT = new RoundedOutlineBorder(Theme.GRAY_200, 1f, Theme.CORNER_RADIUS,
            new Insets(7, 14, 7, 14));
    private static final Border FIELD_SINGLE_FOCUS = new RoundedOutlineBorder(Theme.PRIMARY, 2f, Theme.CORNER_RADIUS,
            new Insets(6, 13, 6, 13));

    private static void attachFieldFocusRing(JComponent field) {
        field.setBorder(FIELD_BORDER_DEFAULT);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(FIELD_BORDER_FOCUS);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(FIELD_BORDER_DEFAULT);
            }
        });
    }

    private static void attachSingleLineFieldFocusRing(JTextField field) {
        field.setBorder(FIELD_SINGLE_DEFAULT);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(FIELD_SINGLE_FOCUS);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(FIELD_SINGLE_DEFAULT);
            }
        });
    }

    /** Fixed height for one-line text fields (avoids tall empty boxes in BoxLayout forms). */
    public static void applySingleLineFieldSize(JTextField field) {
        applySingleLineFieldSize(field, 38);
    }

    public static void applySingleLineFieldSize(JTextField field, int height) {
        Dimension d = field.getPreferredSize();
        field.setPreferredSize(new Dimension(Math.max(d.width, 120), height));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setMinimumSize(new Dimension(80, height));
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.CORNER_RADIUS, Theme.CORNER_RADIUS);
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
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 40));

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
        attachSingleLineFieldFocusRing(field);
        field.setToolTipText(placeholder);
        applySingleLineFieldSize(field);
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(Theme.FONT_BODY);
        attachSingleLineFieldFocusRing(field);
        field.setToolTipText(placeholder);
        applySingleLineFieldSize(field);
        return field;
    }

    public static JTextArea createTextArea(String placeholder, int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setFont(Theme.FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        attachFieldFocusRing(area);
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
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int r = Theme.CORNER_RADIUS_LARGE;
                g2.setColor(new Color(15, 23, 42, 10));
                g2.fillRoundRect(3, 4, w - 3, h - 3, r, r);
                g2.setColor(new Color(15, 23, 42, 16));
                g2.fillRoundRect(2, 3, w - 3, h - 3, r, r);
                g2.setColor(new Color(15, 23, 42, 22));
                g2.fillRoundRect(1, 2, w - 2, h - 2, r, r);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 2, h - 2, r, r);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 3, h - 3, r, r);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        return card;
    }

    /** {@link #createCard()} with wider padding for prominent forms (password settings, etc.). */
    public static JPanel createLargeFormCard() {
        JPanel card = createCard();
        card.setBorder(BorderFactory.createEmptyBorder(32, 44, 40, 44));
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

    public static JComponent createBadge(String text, Color bg, Color fg) {
        final JLabel inner = new JLabel(text);
        inner.setFont(Theme.FONT_SMALL_BOLD);
        inner.setForeground(fg);
        inner.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int r = Math.max(h / 2, 12);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = inner.getPreferredSize();
                return new Dimension(d.width, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        badge.setOpaque(false);
        badge.add(inner);
        return badge;
    }

    public static JComponent createTag(String text) {
        final JLabel inner = new JLabel(text);
        inner.setFont(Theme.FONT_SMALL);
        inner.setForeground(Theme.GRAY_600);
        inner.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JPanel tag = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return inner.getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        tag.setOpaque(false);
        tag.add(inner);
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
        header.setBackground(Theme.WINDOW_BG);
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
        return createFormRow(labelText, field, Theme.FONT_BODY_BOLD, 6);
    }

    /** Roomier label + gap (e.g. password settings). */
    public static JPanel createFormRow(String labelText, JComponent field, Font labelFont, int gapAfterLabel) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(Theme.GRAY_700);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(Box.createVerticalStrut(gapAfterLabel));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(field);
        return row;
    }

    /**
     * Use as {@link BorderLayout#EAST} child so compact widgets (e.g. status badges) are not
     * stretched to the full row height.
     */
    public static JPanel alignTopTrailing(JComponent c) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(c, BorderLayout.NORTH);
        return wrap;
    }

    /** Places a single child in the center of available space (adapts to window size). */
    public static JPanel centerContent(JComponent inner) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        wrap.add(inner, gbc);
        return wrap;
    }
}
