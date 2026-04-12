package com.bupt.ta.ui.panel;

import com.bupt.ta.model.User;
import com.bupt.ta.service.DataService;
import com.bupt.ta.ui.MainFrame;
import com.bupt.ta.ui.Theme;
import com.bupt.ta.ui.component.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private User.Role selectedRole = User.Role.TA;
    private JButton taBtn, moBtn, adminBtn;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(true);
        setBackground(Theme.WINDOW_BG);
        setLayout(new GridLayout(1, 2, 24, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(createLeftPanel());
        add(createRightPanel());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(232, 238, 249), getWidth(), getHeight(), new Color(243, 240, 255));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    /** Call after logout so the next sign-in starts with empty fields. */
    public void clearForm() {
        emailField.setText("");
        passwordField.setText("");
        selectedRole = User.Role.TA;
        updateRoleButtons();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int m = 32;
                float rw = getWidth() - 2f * m;
                float rh = getHeight() - 2f * m;
                float arc = Theme.CORNER_RADIUS_LARGE;
                RoundRectangle2D rr = new RoundRectangle2D.Float(m, m, rw, rh, arc, arc);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 58, 138), getWidth(), getHeight(), new Color(124, 58, 237));
                g2.setPaint(gp);
                g2.fill(rr);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 60, 100, 60));

        JLabel title = new JLabel("<html><b>BUPT TA<br>Recruitment</b></html>");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("<html>Streamline the Teaching Assistant recruitment<br>process for BUPT International School.</html>");
        desc.setForeground(new Color(255, 255, 255, 200));
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(desc);
        panel.add(Box.createVerticalStrut(28));

        JPanel sep = new JPanel();
        sep.setOpaque(true);
        sep.setBackground(new Color(255, 255, 255, 55));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(280, 1));
        sep.setPreferredSize(new Dimension(280, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(28));

        String[] features = {
                "Browse and apply for TA positions",
                "Manage applications efficiently",
                "Track recruitment progress"
        };
        for (String feature : features) {
            panel.add(createFeatureRow(feature));
            panel.add(Box.createVerticalStrut(14));
        }

        return panel;
    }

    private JPanel createFeatureRow(String text) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                int s = 7;
                g2.fillOval((getWidth() - s) / 2, (getHeight() - s) / 2, s, s);
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(18, 22));
        dot.setMaximumSize(new Dimension(18, 22));
        dot.setMinimumSize(new Dimension(18, 22));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(255, 255, 255, 200));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentY(0.55f);

        row.add(dot);
        row.add(Box.createHorizontalStrut(10));
        row.add(label);
        return row;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        JPanel form = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.CORNER_RADIUS_LARGE, Theme.CORNER_RADIUS_LARGE);
                g2.setColor(Theme.GRAY_200);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.CORNER_RADIUS_LARGE, Theme.CORNER_RADIUS_LARGE);
                g2.dispose();
            }
        };
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        form.setPreferredSize(new Dimension(420, 480));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.GRAY_800);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account to continue");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.GRAY_400);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rolePanel = new JPanel(new GridLayout(1, 3, 8, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(340, 40));
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        taBtn = createRoleButton("TA");
        moBtn = createRoleButton("MO");
        adminBtn = createRoleButton("Admin");
        styleActiveRole(taBtn);

        taBtn.addActionListener(e -> { selectedRole = User.Role.TA; updateRoleButtons(); });
        moBtn.addActionListener(e -> { selectedRole = User.Role.MO; updateRoleButtons(); });
        adminBtn.addActionListener(e -> { selectedRole = User.Role.ADMIN; updateRoleButtons(); });

        rolePanel.add(taBtn);
        rolePanel.add(moBtn);
        rolePanel.add(adminBtn);

        emailField = UIHelper.createTextField("Email, TA001, or student ID");
        emailField.setMaximumSize(new Dimension(340, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UIHelper.createPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(340, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = UIHelper.createButton("Sign In", Theme.PRIMARY, Color.WHITE);
        loginBtn.setMaximumSize(new Dimension(340, 44));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());

        JLabel emailLabel = UIHelper.createLabel("Email / Student ID", Theme.FONT_BODY_BOLD, Theme.GRAY_700);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel passLabel = UIHelper.createLabel("Password", Theme.FONT_BODY_BOLD, Theme.GRAY_700);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel registerLink = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLink.setOpaque(false);
        registerLink.setMaximumSize(new Dimension(340, 30));
        registerLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel regText = new JLabel("Don't have an account? ");
        regText.setFont(Theme.FONT_BODY);
        regText.setForeground(Theme.GRAY_400);
        JLabel regLink = new JLabel("Register now");
        regLink.setFont(Theme.FONT_BODY_BOLD);
        regLink.setForeground(Theme.PRIMARY);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showPanel(MainFrame.REGISTER);
            }
        });
        registerLink.add(regText);
        registerLink.add(regLink);

        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(24));
        form.add(rolePanel);
        form.add(Box.createVerticalStrut(20));
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(emailField);
        form.add(Box.createVerticalStrut(16));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(24));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(16));
        form.add(registerLink);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(form, gbc);
        return panel;
    }

    private JButton createRoleButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = Theme.CORNER_RADIUS;
                boolean active = getBackground().getRGB() == Theme.PRIMARY.getRGB();
                if (active) {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
                    g2.setColor(Theme.PRIMARY_DARK);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);
                } else {
                    g2.setColor(Theme.GRAY_100);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
                    g2.setColor(Theme.GRAY_200);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, r, r);
                }
                g2.dispose();
                g.setFont(getFont());
                g.setColor(getForeground());
                FontMetrics fm = g.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 2;
                g.drawString(getText(), tx, ty);
            }
        };
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(Theme.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        styleInactiveRole(btn);
        return btn;
    }

    private void styleActiveRole(JButton btn) {
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.repaint();
    }

    private void styleInactiveRole(JButton btn) {
        btn.setBackground(Theme.GRAY_100);
        btn.setForeground(Theme.GRAY_600);
        btn.repaint();
    }

    private void updateRoleButtons() {
        styleInactiveRole(taBtn);
        styleInactiveRole(moBtn);
        styleInactiveRole(adminBtn);
        switch (selectedRole) {
            case TA: styleActiveRole(taBtn); break;
            case MO: styleActiveRole(moBtn); break;
            case ADMIN: styleActiveRole(adminBtn); break;
        }
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = DataService.getInstance().login(email, password, selectedRole);
        if (user != null) {
            mainFrame.loginSuccess(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
