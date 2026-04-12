package com.bupt.ta.ui.panel;

import com.bupt.ta.model.Application;
import com.bupt.ta.model.Position;
import com.bupt.ta.model.User;
import com.bupt.ta.service.DataService;
import com.bupt.ta.ui.MainFrame;
import com.bupt.ta.ui.Theme;
import com.bupt.ta.ui.component.Sidebar;
import com.bupt.ta.ui.component.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMainPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataService ds;
    private final CardLayout contentLayout;
    private final JPanel contentArea;

    public AdminMainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.ds = DataService.getInstance();
        setName(MainFrame.ADMIN_MAIN);
        setLayout(new BorderLayout());

        User user = ds.getCurrentUser();
        String userName = user != null ? user.getName() : "Admin";

        Sidebar sidebar = new Sidebar(Theme.SIDEBAR_ADMIN, Theme.WARNING, "AD");
        sidebar.addSection("Administration");
        sidebar.addMenuItem("D", "Dashboard");
        sidebar.addMenuItem("V", "Approve Positions");
        sidebar.addMenuItem("T", "Recruitment Stats");
        sidebar.addMenuItem("W", "TA Workload");
        sidebar.addSection("System");
        sidebar.addMenuItem("U", "Manage Users");
        sidebar.addMenuItem("S", "Settings");
        sidebar.addMenuItem("<", "Logout");
        sidebar.addUserSection(userName, "AD");
        sidebar.setActive("Dashboard");

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(Theme.WINDOW_BG);

        contentArea.add(createDashboard(), "Dashboard");
        contentArea.add(createApprovePositions(), "Approve Positions");
        contentArea.add(createRecruitmentStats(), "Recruitment Stats");
        contentArea.add(createTAWorkload(), "TA Workload");
        contentArea.add(createManageUsers(), "Manage Users");
        contentArea.add(createSettingsPanel(), "Settings");

        sidebar.setOnSelect(item -> {
            if ("Logout".equals(item)) {
                mainFrame.logout();
            } else {
                contentLayout.show(contentArea, item);
            }
        });

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        List<Position> allPos = ds.getAllPositions();
        List<Application> allApps = ds.getAllApplications();
        long pending = allPos.stream().filter(p -> p.getStatus() == Position.Status.PENDING_APPROVAL).count();
        long hired = allApps.stream().filter(a -> a.getStatus() == Application.Status.PASSED).count();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Admin Dashboard", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("System overview and pending actions", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Total Positions", String.valueOf(allPos.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Total Applications", String.valueOf(allApps.size()), Theme.GRAY_800));
        JPanel pendingCard = UIHelper.createStatCard("Pending Approval", String.valueOf(pending), Theme.DANGER);
        pendingCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.DANGER),
                pendingCard.getBorder()));
        stats.add(pendingCard);
        stats.add(UIHelper.createStatCard("TAs Hired", String.valueOf(hired), Theme.SUCCESS));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 0));
        bottom.setOpaque(false);

        // Pending approvals
        JPanel pendingPanel = UIHelper.createCard();
        pendingPanel.setLayout(new BoxLayout(pendingPanel, BoxLayout.Y_AXIS));
        pendingPanel.add(UIHelper.createLabel("Pending Position Approvals", Theme.FONT_H3, Theme.GRAY_700));
        pendingPanel.add(Box.createVerticalStrut(12));

        List<Position> pendingPositions = ds.getPendingPositions();
        for (Position pos : pendingPositions) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Theme.DANGER_LIGHT);
            row.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(UIHelper.createLabel(pos.getCourseName() + " TA", Theme.FONT_BODY_BOLD, Theme.GRAY_800));
            info.add(UIHelper.createLabel("By " + pos.getMoName() + " · " + pos.getCourseCode(), Theme.FONT_SMALL, Theme.GRAY_400));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            btns.setOpaque(false);
            JButton approveBtn = UIHelper.createSmallButton("Approve", Theme.SUCCESS, Color.WHITE);
            JButton rejectBtn = UIHelper.createSmallButton("Reject", Theme.DANGER, Color.WHITE);
            approveBtn.addActionListener(e -> {
                pos.setStatus(Position.Status.APPROVED);
                ds.updatePosition(pos);
                mainFrame.showPanel(MainFrame.ADMIN_MAIN);
            });
            rejectBtn.addActionListener(e -> {
                pos.setStatus(Position.Status.REJECTED);
                ds.updatePosition(pos);
                mainFrame.showPanel(MainFrame.ADMIN_MAIN);
            });
            btns.add(approveBtn);
            btns.add(rejectBtn);

            row.add(info, BorderLayout.CENTER);
            row.add(btns, BorderLayout.EAST);
            pendingPanel.add(row);
            pendingPanel.add(Box.createVerticalStrut(8));
        }
        if (pendingPositions.isEmpty()) {
            pendingPanel.add(UIHelper.createLabel("No pending approvals.", Theme.FONT_BODY, Theme.GRAY_400));
        }

        // Chart placeholder
        JPanel chartPanel = UIHelper.createCard();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(UIHelper.createLabel("Applications per Course", Theme.FONT_H3, Theme.GRAY_700), BorderLayout.NORTH);

        JPanel bars = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Map<String, Integer> courseApps = new LinkedHashMap<>();
                for (Application app : allApps) {
                    courseApps.merge(app.getCourseCode(), 1, Integer::sum);
                }

                if (courseApps.isEmpty()) return;

                int maxVal = courseApps.values().stream().max(Integer::compare).orElse(1);
                int barWidth = 50;
                int gap = 20;
                int startX = 40;
                int baseY = getHeight() - 40;
                int maxH = getHeight() - 80;

                int x = startX;
                for (Map.Entry<String, Integer> entry : courseApps.entrySet()) {
                    int barH = (int) ((double) entry.getValue() / maxVal * maxH);
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(x, baseY - barH, barWidth, barH, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.setFont(Theme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    String valStr = String.valueOf(entry.getValue());
                    g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, baseY - barH - 6);
                    g2.setColor(Theme.GRAY_500);
                    g2.setFont(Theme.FONT_SMALL);
                    fm = g2.getFontMetrics();
                    g2.drawString(entry.getKey(), x + (barWidth - fm.stringWidth(entry.getKey())) / 2, baseY + 16);
                    x += barWidth + gap;
                }
            }
        };
        bars.setBackground(Color.WHITE);
        bars.setPreferredSize(new Dimension(300, 200));
        chartPanel.add(bars, BorderLayout.CENTER);

        bottom.add(pendingPanel);
        bottom.add(chartPanel);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createApprovePositions() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Position Approvals", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Review and approve MO-published positions", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        List<Position> allPos = ds.getAllPositions();

        String[] cols = {"Status", "Position", "Course", "MO", "Openings", "Deadline", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Position p : allPos) {
            model.addRow(new Object[]{
                    p.getStatus().name().replace("_", " "),
                    p.getCourseName() + " TA",
                    p.getCourseCode(),
                    p.getMoName(),
                    p.getNumPositions(),
                    p.getDeadline(),
                    p.getStatus() == Position.Status.PENDING_APPROVAL ? "Approve / Reject" : "-"
            });
        }
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);

        JButton approveBtn = UIHelper.createButton("Approve Selected", Theme.SUCCESS, Color.WHITE);
        JButton rejectBtn = UIHelper.createButton("Reject Selected", Theme.DANGER, Color.WHITE);

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < allPos.size()) {
                Position pos = allPos.get(row);
                if (pos.getStatus() == Position.Status.PENDING_APPROVAL) {
                    pos.setStatus(Position.Status.APPROVED);
                    ds.updatePosition(pos);
                    JOptionPane.showMessageDialog(this, "Position approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.showPanel(MainFrame.ADMIN_MAIN);
                }
            }
        });
        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < allPos.size()) {
                Position pos = allPos.get(row);
                if (pos.getStatus() == Position.Status.PENDING_APPROVAL) {
                    pos.setStatus(Position.Status.REJECTED);
                    ds.updatePosition(pos);
                    JOptionPane.showMessageDialog(this, "Position rejected.", "Done", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.showPanel(MainFrame.ADMIN_MAIN);
                }
            }
        });

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(btnPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecruitmentStats() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Recruitment Statistics", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Overall recruitment data and analytics", Theme.FONT_SUBTITLE, Theme.GRAY_400));

        List<Position> allPos = ds.getAllPositions();
        List<Application> allApps = ds.getAllApplications();

        long approved = allPos.stream().filter(p -> p.getStatus() == Position.Status.APPROVED).count();
        long pendingApproval = allPos.stream().filter(p -> p.getStatus() == Position.Status.PENDING_APPROVAL).count();
        long totalApps = allApps.size();
        long passedApps = allApps.stream().filter(a -> a.getStatus() == Application.Status.PASSED).count();
        long pendingApps = allApps.stream().filter(a -> a.getStatus() == Application.Status.PENDING).count();
        long failedApps = allApps.stream().filter(a -> a.getStatus() == Application.Status.FAILED).count();

        JPanel stats = new JPanel(new GridLayout(2, 3, 12, 12));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Total Positions", String.valueOf(allPos.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Approved Positions", String.valueOf(approved), Theme.SUCCESS));
        stats.add(UIHelper.createStatCard("Pending Approval", String.valueOf(pendingApproval), Theme.WARNING));
        stats.add(UIHelper.createStatCard("Total Applications", String.valueOf(totalApps), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Passed", String.valueOf(passedApps), Theme.SUCCESS));
        stats.add(UIHelper.createStatCard("Failed", String.valueOf(failedApps), Theme.DANGER));

        // Per-course breakdown
        JPanel tableCard = UIHelper.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(UIHelper.createLabel("Per-Course Breakdown", Theme.FONT_H3, Theme.GRAY_700), BorderLayout.NORTH);

        String[] cols = {"Course", "Position", "Total Apps", "Passed", "Pending", "Failed", "Hired/Needed"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Position pos : allPos) {
            List<Application> posApps = ds.getApplicationsByPosition(pos.getId());
            long p = posApps.stream().filter(a -> a.getStatus() == Application.Status.PASSED).count();
            long pe = posApps.stream().filter(a -> a.getStatus() == Application.Status.PENDING).count();
            long f = posApps.stream().filter(a -> a.getStatus() == Application.Status.FAILED).count();
            model.addRow(new Object[]{pos.getCourseCode(), pos.getCourseName() + " TA",
                    posApps.size(), p, pe, f, p + " / " + pos.getNumPositions()});
        }
        JTable table = new JTable(model);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTAWorkload() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("TA Workload Overview", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Check TA's overall workload and assignments", Theme.FONT_SUBTITLE, Theme.GRAY_400));

        List<User> tas = ds.getAllTAs();
        List<Application> allApps = ds.getAllApplications();

        String[] cols = {"TA Name", "Student ID", "Programme", "Year", "Assigned Positions", "Total Hours/Week", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (User ta : tas) {
            List<Application> accepted = allApps.stream()
                    .filter(a -> a.getTaId().equals(ta.getId()) && a.getStatus() == Application.Status.PASSED)
                    .collect(Collectors.toList());

            int totalHours = 0;
            StringBuilder posNames = new StringBuilder();
            for (Application app : accepted) {
                Position pos = ds.getPositionById(app.getPositionId());
                if (pos != null) {
                    totalHours += pos.getHoursPerWeek();
                    if (posNames.length() > 0) posNames.append(", ");
                    posNames.append(pos.getCourseCode());
                }
            }

            String status;
            if (totalHours == 0) status = "Unassigned";
            else if (totalHours <= 8) status = "Normal";
            else if (totalHours <= 12) status = "Moderate";
            else status = "Overloaded";

            model.addRow(new Object[]{
                    ta.getName(),
                    ta.getStudentId() != null ? ta.getStudentId() : "-",
                    ta.getProgramme() != null ? ta.getProgramme() : "-",
                    ta.getYearOfStudy() != null ? ta.getYearOfStudy() : "-",
                    posNames.length() > 0 ? posNames.toString() : "-",
                    totalHours,
                    status
            });
        }

        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        // Summary stats
        long unassigned = tas.stream().filter(ta -> allApps.stream().noneMatch(a -> a.getTaId().equals(ta.getId()) && a.getStatus() == Application.Status.PASSED)).count();
        long assigned = tas.size() - unassigned;

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Total TAs", String.valueOf(tas.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Assigned TAs", String.valueOf(assigned), Theme.SUCCESS));
        stats.add(UIHelper.createStatCard("Unassigned TAs", String.valueOf(unassigned), Theme.WARNING));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createManageUsers() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Manage Users", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("View all registered users in the system", Theme.FONT_SUBTITLE, Theme.GRAY_400));

        String[] cols = {"ID", "Name", "Email", "Role", "Programme"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<User> allTAs = ds.getAllTAs();
        for (User u : allTAs) {
            model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole().name(),
                    u.getProgramme() != null ? u.getProgramme() : "-"});
        }

        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Settings", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Change your password", Theme.FONT_SUBTITLE, Theme.GRAY_500));

        JPanel card = UIHelper.createLargeFormCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        int fieldW = 620;
        int fieldH = 48;
        JPasswordField oldPass = UIHelper.createPasswordField("Current password");
        UIHelper.applySingleLineFieldSize(oldPass, fieldH);
        oldPass.setMaximumSize(new Dimension(fieldW, fieldH));
        JPasswordField newPass = UIHelper.createPasswordField("New password");
        UIHelper.applySingleLineFieldSize(newPass, fieldH);
        newPass.setMaximumSize(new Dimension(fieldW, fieldH));
        JPasswordField confirmPass = UIHelper.createPasswordField("Confirm new password");
        UIHelper.applySingleLineFieldSize(confirmPass, fieldH);
        confirmPass.setMaximumSize(new Dimension(fieldW, fieldH));

        JButton changeBtn = UIHelper.createButton("Change Password", Theme.PRIMARY, Color.WHITE);
        changeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        changeBtn.addActionListener(e -> {
            String old = new String(oldPass.getPassword());
            String np = new String(newPass.getPassword());
            String cp = new String(confirmPass.getPassword());
            if (!np.equals(cp)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User user = ds.getCurrentUser();
            if (user != null && ds.changePassword(user.getId(), old, np)) {
                JOptionPane.showMessageDialog(this, "Password changed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(UIHelper.createFormRow("Current Password", oldPass, Theme.FONT_SETTINGS_LABEL, 8));
        card.add(Box.createVerticalStrut(18));
        card.add(UIHelper.createFormRow("New Password", newPass, Theme.FONT_SETTINGS_LABEL, 8));
        card.add(Box.createVerticalStrut(18));
        card.add(UIHelper.createFormRow("Confirm New Password", confirmPass, Theme.FONT_SETTINGS_LABEL, 8));
        card.add(Box.createVerticalStrut(28));
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.setPreferredSize(new Dimension(fieldW, 52));
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        card.add(changeBtn);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(UIHelper.centerContent(card), BorderLayout.CENTER);
        return panel;
    }
}
