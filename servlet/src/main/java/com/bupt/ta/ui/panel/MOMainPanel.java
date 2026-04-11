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
import java.util.Arrays;
import java.util.List;

public class MOMainPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataService ds;
    private final CardLayout contentLayout;
    private final JPanel contentArea;

    public MOMainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.ds = DataService.getInstance();
        setName(MainFrame.MO_MAIN);
        setLayout(new BorderLayout());

        User user = ds.getCurrentUser();
        String userName = user != null ? user.getName() : "MO";

        Sidebar sidebar = new Sidebar(Theme.SIDEBAR_MO, Theme.SECONDARY, "MO");
        sidebar.addSection("Management");
        sidebar.addMenuItem("\u2302", "Dashboard");
        sidebar.addMenuItem("\u271A", "Publish Position");
        sidebar.addMenuItem("\uD83D\uDCBC", "My Positions");
        sidebar.addMenuItem("\uD83D\uDC65", "Review Applications");
        sidebar.addMenuItem("\uD83D\uDCE7", "Offer Letters");
        sidebar.addSection("Account");
        sidebar.addMenuItem("\u2699", "Settings");
        sidebar.addMenuItem("\u27A1", "Logout");
        sidebar.addUserSection(userName, userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO");
        sidebar.setActive("Dashboard");

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(Theme.GRAY_50);

        contentArea.add(createDashboard(), "Dashboard");
        contentArea.add(createPublishPosition(), "Publish Position");
        contentArea.add(createMyPositions(), "My Positions");
        contentArea.add(createReviewApplications(), "Review Applications");
        contentArea.add(createOfferLetters(), "Offer Letters");
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
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        User user = ds.getCurrentUser();
        List<Position> myPositions = user != null ? ds.getPositionsByMo(user.getId()) : List.of();
        List<Application> allApps = ds.getAllApplications();
        long myApps = allApps.stream().filter(a -> myPositions.stream().anyMatch(p -> p.getId().equals(a.getPositionId()))).count();
        long pendingApps = allApps.stream().filter(a -> myPositions.stream().anyMatch(p -> p.getId().equals(a.getPositionId())) && a.getStatus() == Application.Status.PENDING).count();
        long hired = allApps.stream().filter(a -> myPositions.stream().anyMatch(p -> p.getId().equals(a.getPositionId())) && a.getStatus() == Application.Status.PASSED).count();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Welcome, " + (user != null ? user.getName() : "MO"), Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Here's an overview of your TA recruitment", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        JButton publishBtn = UIHelper.createButton("+ Publish New Position", Theme.PRIMARY, Color.WHITE);
        publishBtn.addActionListener(e -> contentLayout.show(contentArea, "Publish Position"));
        top.add(publishBtn, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Active Positions", String.valueOf(myPositions.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Total Applications", String.valueOf(myApps), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Pending Review", String.valueOf(pendingApps), Theme.WARNING));
        stats.add(UIHelper.createStatCard("TAs Hired", String.valueOf(hired), Theme.SUCCESS));

        String[] cols = {"Position", "Course", "Applicants", "Hired", "Status", "Deadline", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Position p : myPositions) {
            long appCount = allApps.stream().filter(a -> a.getPositionId().equals(p.getId())).count();
            long hiredCount = ds.countHiredTAsForPosition(p.getId());
            model.addRow(new Object[]{
                    p.getCourseName() + " TA", p.getCourseCode(),
                    appCount, hiredCount + " / " + p.getNumPositions(),
                    p.getStatus().name().replace("_", " "), p.getDeadline(), "Review"
            });
        }
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPublishPosition() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Publish New TA Position", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Fill in the details to create a new TA recruitment post", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);

        JPanel leftCard = UIHelper.createCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.add(UIHelper.createLabel("Position Details", Theme.FONT_H3, Theme.GRAY_700));
        leftCard.add(Box.createVerticalStrut(12));

        JTextField courseNameField = UIHelper.createTextField("e.g., Software Engineering");
        courseNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField courseCodeField = UIHelper.createTextField("e.g., EBU6304");
        courseCodeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField numField = UIHelper.createTextField("Number of TAs required");
        numField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField hoursField = UIHelper.createTextField("Hours per week (0 for flexible)");
        hoursField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextField deadlineField = UIHelper.createTextField("YYYY-MM-DD");
        deadlineField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextArea dutiesArea = UIHelper.createTextArea("Duties & Responsibilities", 4);

        leftCard.add(UIHelper.createFormRow("Course Name *", courseNameField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Course Code *", courseCodeField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Number of TAs Required *", numField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Hours per Week *", hoursField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Application Deadline *", deadlineField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Duties & Responsibilities *", new JScrollPane(dutiesArea)));

        JPanel rightCard = UIHelper.createCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.add(UIHelper.createLabel("Skill Requirements", Theme.FONT_H3, Theme.GRAY_700));
        rightCard.add(Box.createVerticalStrut(12));

        JTextField skillsField = UIHelper.createTextField("Comma-separated skills (e.g., Java, Agile)");
        skillsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JTextArea prefArea = UIHelper.createTextArea("Preferred qualifications...", 4);

        rightCard.add(UIHelper.createFormRow("Required Skills *", skillsField));
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(UIHelper.createFormRow("Preferred Qualifications", new JScrollPane(prefArea)));
        rightCard.add(Box.createVerticalStrut(16));

        JButton publishBtn = UIHelper.createButton("Publish Position", Theme.PRIMARY, Color.WHITE);
        publishBtn.addActionListener(e -> {
            if (courseNameField.getText().trim().isEmpty() || courseCodeField.getText().trim().isEmpty()
                    || numField.getText().trim().isEmpty() || deadlineField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                User user = ds.getCurrentUser();
                Position pos = new Position();
                pos.setCourseName(courseNameField.getText().trim());
                pos.setCourseCode(courseCodeField.getText().trim());
                pos.setDepartment("International School");
                pos.setNumPositions(Integer.parseInt(numField.getText().trim()));
                pos.setHoursPerWeek(Integer.parseInt(hoursField.getText().trim()));
                pos.setPayRate("¥50/hr");
                pos.setDeadline(deadlineField.getText().trim());
                String skills = skillsField.getText().trim();
                if (!skills.isEmpty()) {
                    pos.setRequiredSkills(Arrays.asList(skills.split("\\s*,\\s*")));
                }
                pos.setDuties(dutiesArea.getText().trim());
                pos.setPreferredQualifications(prefArea.getText().trim());
                pos.setMoId(user != null ? user.getId() : "");
                pos.setMoName(user != null ? user.getName() : "");
                pos.setStatus(Position.Status.PENDING_APPROVAL);
                ds.addPosition(pos);
                JOptionPane.showMessageDialog(this, "Position published! Waiting for admin approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel(MainFrame.MO_MAIN);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        rightCard.add(publishBtn);
        rightCard.add(Box.createVerticalStrut(12));

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(Theme.WARNING_LIGHT);
        notePanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        notePanel.add(UIHelper.createLabel("Note: After publishing, the position will be sent to Admin for approval.", Theme.FONT_SMALL, Theme.WARNING));
        rightCard.add(notePanel);

        grid.add(leftCard);
        grid.add(rightCard);

        panel.add(top, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyPositions() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("My Positions", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("All positions you have published", Theme.FONT_SUBTITLE, Theme.GRAY_400));

        User user = ds.getCurrentUser();
        List<Position> myPos = user != null ? ds.getPositionsByMo(user.getId()) : List.of();

        String[] cols = {"ID", "Position", "Course", "Openings", "Hours/Week", "Status", "Deadline"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Position p : myPos) {
            model.addRow(new Object[]{p.getId(), p.getCourseName() + " TA", p.getCourseCode(),
                    p.getNumPositions(), p.getHoursPerWeek() == 0 ? "Flexible" : p.getHoursPerWeek(),
                    p.getStatus().name().replace("_", " "), p.getDeadline()});
        }
        JTable table = new JTable(model);
        UIHelper.styleTable(table);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReviewApplications() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Review TA Applications", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Review and manage applications for your positions", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        User user = ds.getCurrentUser();
        List<Position> myPositions = user != null ? ds.getPositionsByMo(user.getId()) : List.of();

        JPanel applicantList = new JPanel();
        applicantList.setLayout(new BoxLayout(applicantList, BoxLayout.Y_AXIS));
        applicantList.setOpaque(false);

        JComboBox<String> positionFilter = new JComboBox<>();
        positionFilter.addItem("All Positions");
        for (Position p : myPositions) {
            positionFilter.addItem(p.getCourseName() + " TA - " + p.getCourseCode());
        }
        positionFilter.setFont(Theme.FONT_BODY);
        positionFilter.setMaximumSize(new Dimension(400, 36));
        top.add(positionFilter, BorderLayout.EAST);

        Runnable refreshList = () -> {
            applicantList.removeAll();
            int selectedIdx = positionFilter.getSelectedIndex();
            List<Application> apps;
            if (selectedIdx <= 0) {
                apps = ds.getAllApplications().stream()
                        .filter(a -> myPositions.stream().anyMatch(p -> p.getId().equals(a.getPositionId())))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                Position selPos = myPositions.get(selectedIdx - 1);
                apps = ds.getApplicationsByPosition(selPos.getId());
            }

            for (Application app : apps) {
                applicantList.add(createApplicantCard(app));
                applicantList.add(Box.createVerticalStrut(8));
            }
            if (apps.isEmpty()) {
                applicantList.add(UIHelper.createLabel("No applications found.", Theme.FONT_BODY, Theme.GRAY_400));
            }
            applicantList.revalidate();
            applicantList.repaint();
        };

        positionFilter.addActionListener(e -> refreshList.run());
        refreshList.run();

        JScrollPane scroll = new JScrollPane(applicantList);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createApplicantCard(Application app) {
        JPanel card = UIHelper.createCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        User ta = ds.getUserById(app.getTaId());
        String[] nameParts = app.getTaName().split(" ");
        String initials = nameParts.length >= 2
                ? (nameParts[0].substring(0, 1) + nameParts[1].substring(0, 1)).toUpperCase()
                : app.getTaName().substring(0, Math.min(2, app.getTaName().length())).toUpperCase();

        JLabel avatar = new JLabel(initials.toUpperCase());
        avatar.setOpaque(true);
        avatar.setBackground(app.getStatus() == Application.Status.PASSED ? Theme.SUCCESS :
                app.getStatus() == Application.Status.FAILED ? Theme.DANGER : Theme.PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameRow.setOpaque(false);
        nameRow.add(UIHelper.createLabel(app.getTaName(), Theme.FONT_BODY_BOLD, Theme.GRAY_800));
        Color badgeBg = app.getStatus() == Application.Status.PASSED ? Theme.SUCCESS_LIGHT :
                app.getStatus() == Application.Status.FAILED ? Theme.DANGER_LIGHT : Theme.WARNING_LIGHT;
        Color badgeFg = app.getStatus() == Application.Status.PASSED ? Theme.SUCCESS :
                app.getStatus() == Application.Status.FAILED ? Theme.DANGER : Theme.WARNING;
        nameRow.add(UIHelper.createBadge(app.getStatus().name(), badgeBg, badgeFg));
        infoPanel.add(nameRow);

        String detail = "";
        if (ta != null) {
            detail = (ta.getProgramme() != null ? ta.getProgramme() : "") +
                    (ta.getYearOfStudy() != null ? " " + ta.getYearOfStudy() : "") +
                    (ta.getGpa() != null ? " · GPA " + ta.getGpa() : "") +
                    (ta.getSkills() != null ? " · Skills: " + String.join(", ", ta.getSkills()) : "");
        }
        detail += " · " + app.getPositionTitle() + " · Applied: " + app.getAppliedDate();
        infoPanel.add(UIHelper.createLabel(detail, Theme.FONT_SMALL, Theme.GRAY_400));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        if (app.getStatus() == Application.Status.PENDING) {
            JButton passBtn = UIHelper.createSmallButton("Pass", Theme.SUCCESS, Color.WHITE);
            passBtn.addActionListener(e -> {
                String feedback = JOptionPane.showInputDialog(this, "Enter feedback for the applicant:", "Feedback", JOptionPane.PLAIN_MESSAGE);
                if (feedback != null) {
                    app.setStatus(Application.Status.PASSED);
                    app.setFeedback(feedback.isEmpty() ? "Congratulations! You have been selected." : feedback);
                    ds.updateApplication(app);
                    mainFrame.showPanel(MainFrame.MO_MAIN);
                }
            });
            JButton failBtn = UIHelper.createSmallButton("Fail", Theme.DANGER, Color.WHITE);
            failBtn.addActionListener(e -> {
                String feedback = JOptionPane.showInputDialog(this, "Enter feedback:", "Feedback", JOptionPane.PLAIN_MESSAGE);
                if (feedback != null) {
                    app.setStatus(Application.Status.FAILED);
                    app.setFeedback(feedback.isEmpty() ? "Unfortunately, you were not selected." : feedback);
                    ds.updateApplication(app);
                    mainFrame.showPanel(MainFrame.MO_MAIN);
                }
            });
            actions.add(passBtn);
            actions.add(failBtn);
        } else {
            JLabel statusLabel = UIHelper.createLabel(app.getStatus().name(), Theme.FONT_SMALL_BOLD,
                    app.getStatus() == Application.Status.PASSED ? Theme.SUCCESS : Theme.DANGER);
            actions.add(statusLabel);
        }

        card.add(avatar, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    private JPanel createOfferLetters() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Generate Offer Letters", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Select passed candidates and generate offer letters", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        User user = ds.getCurrentUser();
        List<Position> myPositions = user != null ? ds.getPositionsByMo(user.getId()) : List.of();

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);

        JPanel leftCard = UIHelper.createCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.add(UIHelper.createLabel("Select Candidates", Theme.FONT_H3, Theme.GRAY_700));
        leftCard.add(Box.createVerticalStrut(12));

        JComboBox<String> posCombo = new JComboBox<>();
        for (Position p : myPositions) {
            posCombo.addItem(p.getCourseName() + " TA - " + p.getCourseCode());
        }
        posCombo.setFont(Theme.FONT_BODY);
        posCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftCard.add(UIHelper.createFormRow("Position", posCombo));
        leftCard.add(Box.createVerticalStrut(12));

        JPanel candidateList = new JPanel();
        candidateList.setLayout(new BoxLayout(candidateList, BoxLayout.Y_AXIS));
        candidateList.setOpaque(false);

        // Right card - preview
        JPanel rightCard = UIHelper.createCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.add(UIHelper.createLabel("Offer Letter Preview", Theme.FONT_H3, Theme.GRAY_700));
        rightCard.add(Box.createVerticalStrut(12));

        JTextArea previewArea = new JTextArea();
        previewArea.setFont(Theme.FONT_BODY);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        JTextField startDateField = UIHelper.createTextField("YYYY-MM-DD");
        startDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        startDateField.setText("2026-04-14");

        Runnable refreshCandidates = () -> {
            candidateList.removeAll();
            if (posCombo.getSelectedIndex() >= 0 && posCombo.getSelectedIndex() < myPositions.size()) {
                Position selPos = myPositions.get(posCombo.getSelectedIndex());
                List<Application> passed = ds.getApplicationsByPosition(selPos.getId()).stream()
                        .filter(a -> a.getStatus() == Application.Status.PASSED)
                        .collect(java.util.stream.Collectors.toList());
                for (Application app : passed) {
                    JPanel row = new JPanel(new BorderLayout());
                    row.setBackground(Theme.SUCCESS_LIGHT);
                    row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                    row.add(UIHelper.createLabel("\u2611 " + app.getTaName(), Theme.FONT_BODY_BOLD, Theme.GRAY_800), BorderLayout.WEST);
                    candidateList.add(row);
                    candidateList.add(Box.createVerticalStrut(4));
                }
                if (!passed.isEmpty()) {
                    Application first = passed.get(0);
                    Position pos = selPos;
                    previewArea.setText("BUPT International School\n" +
                            "Teaching Assistant Offer Letter\n\n" +
                            "Dear " + first.getTaName() + ",\n\n" +
                            "We are pleased to offer you the position of Teaching Assistant for the course " +
                            pos.getCourseCode() + " - " + pos.getCourseName() + " at BUPT International School.\n\n" +
                            "Start Date: " + startDateField.getText() + "\n" +
                            "Working Hours: " + pos.getHoursPerWeek() + " hours/week\n" +
                            "Compensation: " + pos.getPayRate() + "\n\n" +
                            "Please confirm your acceptance by replying to this offer.\n\n" +
                            "Best regards,\n" +
                            (ds.getCurrentUser() != null ? ds.getCurrentUser().getName() : "Module Organiser") + "\n" +
                            "Module Organiser");
                }
                if (passed.isEmpty()) {
                    candidateList.add(UIHelper.createLabel("No passed candidates.", Theme.FONT_BODY, Theme.GRAY_400));
                    previewArea.setText("");
                }
            }
            candidateList.revalidate();
            candidateList.repaint();
        };

        posCombo.addActionListener(e -> refreshCandidates.run());
        if (!myPositions.isEmpty()) refreshCandidates.run();

        leftCard.add(candidateList);
        leftCard.add(Box.createVerticalStrut(12));
        leftCard.add(UIHelper.createLabel("Offer Details", Theme.FONT_H3, Theme.GRAY_700));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Start Date", startDateField));
        leftCard.add(Box.createVerticalStrut(12));

        JButton generateBtn = UIHelper.createButton("Generate Offers", Theme.PRIMARY, Color.WHITE);
        generateBtn.addActionListener(e -> {
            refreshCandidates.run();
            JOptionPane.showMessageDialog(this, "Offer letters generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        leftCard.add(generateBtn);

        rightCard.add(new JScrollPane(previewArea));

        grid.add(leftCard);
        grid.add(rightCard);

        panel.add(top, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Settings", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Change your password", Theme.FONT_SUBTITLE, Theme.GRAY_400));

        JPanel card = UIHelper.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(400, 300));

        JPasswordField oldPass = UIHelper.createPasswordField("Current password");
        oldPass.setMaximumSize(new Dimension(400, 38));
        JPasswordField newPass = UIHelper.createPasswordField("New password");
        newPass.setMaximumSize(new Dimension(400, 38));
        JPasswordField confirmPass = UIHelper.createPasswordField("Confirm new password");
        confirmPass.setMaximumSize(new Dimension(400, 38));

        JButton changeBtn = UIHelper.createButton("Change Password", Theme.PRIMARY, Color.WHITE);
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

        card.add(UIHelper.createFormRow("Current Password", oldPass));
        card.add(Box.createVerticalStrut(12));
        card.add(UIHelper.createFormRow("New Password", newPass));
        card.add(Box.createVerticalStrut(12));
        card.add(UIHelper.createFormRow("Confirm New Password", confirmPass));
        card.add(Box.createVerticalStrut(16));
        card.add(changeBtn);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.add(card);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }
}
