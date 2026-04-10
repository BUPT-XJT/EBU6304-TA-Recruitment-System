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
import java.util.List;

public class TAMainPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataService ds;
    private final CardLayout contentLayout;
    private final JPanel contentArea;

    public TAMainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.ds = DataService.getInstance();
        setName(MainFrame.TA_MAIN);
        setLayout(new BorderLayout());

        User user = ds.getCurrentUser();
        String userName = user != null ? user.getName() : "TA";
        String initials = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "TA";

        Sidebar sidebar = new Sidebar(Theme.SIDEBAR_TA, Theme.PRIMARY, "TA");
        sidebar.addSection("Main Menu");
        sidebar.addMenuItem("\u2302", "Dashboard");
        sidebar.addMenuItem("\uD83D\uDC64", "My Profile");
        sidebar.addMenuItem("\uD83D\uDCBC", "Browse Positions");
        sidebar.addMenuItem("\uD83D\uDCC4", "My Applications");
        sidebar.addSection("Account");
        sidebar.addMenuItem("\u2699", "Settings");
        sidebar.addMenuItem("\u27A1", "Logout");
        sidebar.addUserSection(userName, initials);
        sidebar.setActive("Dashboard");

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(Theme.GRAY_50);

        contentArea.add(createDashboard(), "Dashboard");
        contentArea.add(createProfilePanel(), "My Profile");
        contentArea.add(createBrowsePositions(), "Browse Positions");
        contentArea.add(createMyApplications(), "My Applications");
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
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = UIHelper.createLabel("Welcome, " + (user != null ? user.getName() : "TA"), Theme.FONT_TITLE, Theme.GRAY_800);
        JLabel desc = UIHelper.createLabel("Here's an overview of your TA activity", Theme.FONT_SUBTITLE, Theme.GRAY_400);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(desc);
        top.add(titleBox, BorderLayout.WEST);

        List<Application> myApps = user != null ? ds.getApplicationsByTa(user.getId()) : List.of();
        long pending = myApps.stream().filter(a -> a.getStatus() == Application.Status.PENDING).count();
        long passed = myApps.stream().filter(a -> a.getStatus() == Application.Status.PASSED).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Total Applications", String.valueOf(myApps.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Pending Review", String.valueOf(pending), Theme.WARNING));
        stats.add(UIHelper.createStatCard("Accepted", String.valueOf(passed), Theme.SUCCESS));

        JPanel openPositions = UIHelper.createCard();
        openPositions.setLayout(new BorderLayout());
        JLabel posTitle = UIHelper.createLabel("Available Positions", Theme.FONT_H3, Theme.GRAY_700);
        posTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        openPositions.add(posTitle, BorderLayout.NORTH);

        List<Position> approved = ds.getApprovedPositions();
        String[] cols = {"Position", "Course", "Openings", "Hours/Week", "Deadline"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Position p : approved) {
            model.addRow(new Object[]{p.getCourseName() + " TA", p.getCourseCode(), p.getNumPositions(),
                    p.getHoursPerWeek() == 0 ? "Flexible" : p.getHoursPerWeek(), p.getDeadline()});
        }
        JTable table = new JTable(model);
        UIHelper.styleTable(table);
        openPositions.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(openPositions, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        User user = ds.getCurrentUser();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("My Profile", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Complete your profile to apply for TA positions", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        JButton saveBtn = UIHelper.createButton("Save Changes", Theme.PRIMARY, Color.WHITE);
        top.add(saveBtn, BorderLayout.EAST);

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);

        // Left card - personal info
        JPanel leftCard = UIHelper.createCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.add(UIHelper.createLabel("Personal Information", Theme.FONT_H3, Theme.GRAY_700));
        leftCard.add(Box.createVerticalStrut(12));

        JTextField nameField = UIHelper.createTextField("Full Name");
        if (user != null) nameField.setText(user.getName());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextField progField = UIHelper.createTextField("Programme");
        if (user != null && user.getProgramme() != null) progField.setText(user.getProgramme());
        progField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextField yearField = UIHelper.createTextField("Year of Study");
        if (user != null && user.getYearOfStudy() != null) yearField.setText(user.getYearOfStudy());
        yearField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextField skillsField = UIHelper.createTextField("Skills (comma separated)");
        if (user != null && user.getSkills() != null) skillsField.setText(String.join(", ", user.getSkills()));
        skillsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextArea expArea = UIHelper.createTextArea("Previous TA experience", 3);
        if (user != null && user.getExperience() != null) expArea.setText(user.getExperience());

        leftCard.add(UIHelper.createFormRow("Full Name", nameField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Programme", progField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Year of Study", yearField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Skills", skillsField));
        leftCard.add(Box.createVerticalStrut(8));
        leftCard.add(UIHelper.createFormRow("Previous TA Experience", new JScrollPane(expArea)));

        // Right card - CV & education
        JPanel rightCard = UIHelper.createCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.add(UIHelper.createLabel("Upload CV / Resume", Theme.FONT_H3, Theme.GRAY_700));
        rightCard.add(Box.createVerticalStrut(12));

        JPanel uploadBox = new JPanel();
        uploadBox.setBackground(Theme.GRAY_50);
        uploadBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(Theme.GRAY_300, 4, 4),
                BorderFactory.createEmptyBorder(24, 20, 24, 20)));
        uploadBox.setLayout(new BoxLayout(uploadBox, BoxLayout.Y_AXIS));
        uploadBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel uploadIcon = new JLabel("\uD83D\uDCC4", SwingConstants.CENTER);
        uploadIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        uploadIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel uploadText = new JLabel("Drag & drop your CV here, or browse");
        uploadText.setFont(Theme.FONT_BODY);
        uploadText.setForeground(Theme.GRAY_500);
        uploadText.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel uploadHint = new JLabel("Supports PDF, DOC, DOCX (Max 10MB)");
        uploadHint.setFont(Theme.FONT_SMALL);
        uploadHint.setForeground(Theme.GRAY_400);
        uploadHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBox.add(uploadIcon);
        uploadBox.add(uploadText);
        uploadBox.add(uploadHint);

        JButton browseBtn = UIHelper.createButton("Choose File", Theme.PRIMARY, Color.WHITE);
        browseBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel cvStatus = new JLabel();
        cvStatus.setFont(Theme.FONT_BODY);
        cvStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (user != null && user.getCvFileName() != null && !user.getCvFileName().isEmpty()) {
            cvStatus.setText("\u2713 " + user.getCvFileName() + " uploaded");
            cvStatus.setForeground(Theme.SUCCESS);
        }

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getName();
                if (user != null) {
                    user.setCvFileName(fileName);
                    ds.updateUser(user);
                }
                cvStatus.setText("\u2713 " + fileName + " uploaded successfully");
                cvStatus.setForeground(Theme.SUCCESS);
            }
        });

        rightCard.add(uploadBox);
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(browseBtn);
        rightCard.add(Box.createVerticalStrut(4));
        rightCard.add(cvStatus);
        rightCard.add(Box.createVerticalStrut(16));

        rightCard.add(UIHelper.createLabel("Education Background", Theme.FONT_H3, Theme.GRAY_700));
        rightCard.add(Box.createVerticalStrut(8));

        JTextField uniField = UIHelper.createTextField("University");
        if (user != null && user.getUniversity() != null) uniField.setText(user.getUniversity());
        uniField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextField gpaField = UIHelper.createTextField("GPA");
        if (user != null && user.getGpa() != null) gpaField.setText(user.getGpa());
        gpaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        rightCard.add(UIHelper.createFormRow("University", uniField));
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(UIHelper.createFormRow("GPA", gpaField));

        saveBtn.addActionListener(e -> {
            if (user != null) {
                user.setName(nameField.getText().trim());
                user.setProgramme(progField.getText().trim());
                user.setYearOfStudy(yearField.getText().trim());
                String skillStr = skillsField.getText().trim();
                if (!skillStr.isEmpty()) {
                    user.setSkills(java.util.Arrays.asList(skillStr.split("\\s*,\\s*")));
                }
                user.setExperience(expArea.getText().trim());
                user.setUniversity(uniField.getText().trim());
                user.setGpa(gpaField.getText().trim());
                ds.updateUser(user);
                JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        grid.add(leftCard);
        grid.add(rightCard);

        panel.add(top, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBrowsePositions() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Available TA Positions", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Browse and apply for Teaching Assistant roles", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        JTextField searchField = UIHelper.createTextField("Search by course name, skills, or keywords...");
        searchField.setPreferredSize(new Dimension(400, 36));
        top.add(searchField, BorderLayout.SOUTH);

        List<Position> positions = ds.getApprovedPositions();
        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);

        for (Position pos : positions) {
            grid.add(createPositionCard(pos));
        }

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            grid.removeAll();
            for (Position pos : positions) {
                if (query.isEmpty()
                        || pos.getCourseName().toLowerCase().contains(query)
                        || pos.getCourseCode().toLowerCase().contains(query)
                        || pos.getRequiredSkills().stream().anyMatch(s -> s.toLowerCase().contains(query))) {
                    grid.add(createPositionCard(pos));
                }
            }
            grid.revalidate();
            grid.repaint();
        });

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPositionCard(Position pos) {
        JPanel card = UIHelper.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel title = UIHelper.createLabel(pos.getCourseName() + " TA", Theme.FONT_BODY_BOLD, Theme.GRAY_800);
        JLabel badge;
        if (pos.getStatus() == Position.Status.APPROVED) {
            badge = UIHelper.createBadge("Open", Theme.SUCCESS_LIGHT, Theme.SUCCESS);
        } else {
            badge = UIHelper.createBadge("Closed", Theme.GRAY_200, Theme.GRAY_500);
        }
        topRow.add(title, BorderLayout.WEST);
        topRow.add(badge, BorderLayout.EAST);

        JLabel dept = UIHelper.createLabel(pos.getCourseCode() + " - " + pos.getDepartment(), Theme.FONT_SMALL, Theme.GRAY_400);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        info.setOpaque(false);
        info.add(UIHelper.createLabel("\uD83D\uDC65 " + pos.getNumPositions() + " positions", Theme.FONT_SMALL, Theme.GRAY_500));
        info.add(UIHelper.createLabel("\u23F0 " + (pos.getHoursPerWeek() == 0 ? "Flexible" : pos.getHoursPerWeek() + " hrs/week"), Theme.FONT_SMALL, Theme.GRAY_500));
        info.add(UIHelper.createLabel("\uD83D\uDCB0 " + pos.getPayRate(), Theme.FONT_SMALL, Theme.GRAY_500));

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tagsPanel.setOpaque(false);
        for (String skill : pos.getRequiredSkills()) {
            tagsPanel.add(UIHelper.createTag(skill));
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel deadline = UIHelper.createLabel("Deadline: " + pos.getDeadline(), Theme.FONT_SMALL, Theme.GRAY_400);
        JButton applyBtn = UIHelper.createSmallButton("View & Apply", Theme.PRIMARY, Color.WHITE);
        applyBtn.addActionListener(e -> showPositionDetail(pos));
        bottom.add(deadline, BorderLayout.WEST);
        bottom.add(applyBtn, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createVerticalStrut(4));
        card.add(dept);
        card.add(Box.createVerticalStrut(8));
        card.add(info);
        card.add(Box.createVerticalStrut(6));
        card.add(tagsPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(bottom);

        return card;
    }

    private void showPositionDetail(Position pos) {
        User user = ds.getCurrentUser();
        boolean alreadyApplied = user != null && ds.hasApplied(user.getId(), pos.getId());

        JPanel detailPanel = new JPanel(new BorderLayout(16, 0));
        detailPanel.setPreferredSize(new Dimension(650, 450));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.add(UIHelper.createLabel(pos.getCourseName() + " TA", new Font("Segoe UI", Font.BOLD, 20), Theme.GRAY_800));
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(UIHelper.createLabel(pos.getCourseCode() + " - " + pos.getDepartment(), Theme.FONT_BODY, Theme.GRAY_400));
        leftPanel.add(Box.createVerticalStrut(16));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 8, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(600, 60));
        statsRow.add(createMiniStat("Positions", String.valueOf(pos.getNumPositions())));
        statsRow.add(createMiniStat("Hours/Week", pos.getHoursPerWeek() == 0 ? "Flex" : String.valueOf(pos.getHoursPerWeek())));
        statsRow.add(createMiniStat("Deadline", pos.getDeadline()));
        leftPanel.add(statsRow);
        leftPanel.add(Box.createVerticalStrut(12));

        leftPanel.add(UIHelper.createLabel("Required Skills", Theme.FONT_BODY_BOLD, Theme.GRAY_700));
        leftPanel.add(Box.createVerticalStrut(4));
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        tags.setOpaque(false);
        for (String s : pos.getRequiredSkills()) tags.add(UIHelper.createTag(s));
        tags.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(tags);
        leftPanel.add(Box.createVerticalStrut(8));

        leftPanel.add(UIHelper.createLabel("Responsibilities", Theme.FONT_BODY_BOLD, Theme.GRAY_700));
        leftPanel.add(Box.createVerticalStrut(4));
        JTextArea duties = new JTextArea(pos.getDuties());
        duties.setFont(Theme.FONT_BODY);
        duties.setForeground(Theme.GRAY_600);
        duties.setEditable(false);
        duties.setLineWrap(true);
        duties.setWrapStyleWord(true);
        duties.setOpaque(false);
        leftPanel.add(duties);
        leftPanel.add(Box.createVerticalStrut(8));

        leftPanel.add(UIHelper.createLabel("Module Organiser: " + pos.getMoName(), Theme.FONT_BODY, Theme.GRAY_600));

        detailPanel.add(new JScrollPane(leftPanel), BorderLayout.CENTER);

        // Right panel - apply
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(240, 0));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        rightPanel.add(UIHelper.createLabel("Submit Application", Theme.FONT_H3, Theme.GRAY_700));
        rightPanel.add(Box.createVerticalStrut(12));

        if (user != null && user.getCvFileName() != null && !user.getCvFileName().isEmpty()) {
            JLabel cvLabel = UIHelper.createLabel("\uD83D\uDCC4 " + user.getCvFileName(), Theme.FONT_BODY, Theme.GRAY_600);
            rightPanel.add(cvLabel);
        } else {
            rightPanel.add(UIHelper.createLabel("No CV uploaded yet.", Theme.FONT_BODY, Theme.DANGER));
        }

        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(UIHelper.createLabel("Additional Notes (Optional)", Theme.FONT_BODY_BOLD, Theme.GRAY_700));
        JTextArea notes = UIHelper.createTextArea("Why you're a good fit...", 4);
        rightPanel.add(new JScrollPane(notes));
        rightPanel.add(Box.createVerticalStrut(12));

        if (alreadyApplied) {
            rightPanel.add(UIHelper.createLabel("You have already applied.", Theme.FONT_BODY_BOLD, Theme.SUCCESS));
        } else {
            JButton submitBtn = UIHelper.createButton("Submit Application", Theme.PRIMARY, Color.WHITE);
            submitBtn.addActionListener(e -> {
                if (user == null) return;
                Application app = new Application();
                app.setTaId(user.getId());
                app.setTaName(user.getName());
                app.setPositionId(pos.getId());
                app.setPositionTitle(pos.getCourseName() + " TA");
                app.setCourseCode(pos.getCourseCode());
                app.setAppliedDate(ds.getTodayDate());
                app.setAdditionalNotes(notes.getText().trim());
                app.setStatus(Application.Status.PENDING);
                app.setFeedback("Under review");
                ds.addApplication(app);
                JOptionPane.showMessageDialog(detailPanel, "Application submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(detailPanel).dispose();
                mainFrame.showPanel(MainFrame.TA_MAIN);
            });
            rightPanel.add(submitBtn);
        }

        detailPanel.add(rightPanel, BorderLayout.EAST);

        JOptionPane.showMessageDialog(this, detailPanel, "Position Detail", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createMiniStat(String label, String value) {
        JPanel p = new JPanel();
        p.setBackground(Theme.GRAY_50);
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = UIHelper.createLabel(label, Theme.FONT_SMALL, Theme.GRAY_400);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel val = UIHelper.createLabel(value, Theme.FONT_BODY_BOLD, Theme.GRAY_800);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl);
        p.add(val);
        return p;
    }

    private JPanel createMyApplications() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.GRAY_50);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("My Applications", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Track the status of all your TA applications", Theme.FONT_SUBTITLE, Theme.GRAY_400));
        top.add(titleBox, BorderLayout.WEST);

        User user = ds.getCurrentUser();
        List<Application> apps = user != null ? ds.getApplicationsByTa(user.getId()) : List.of();
        long pending = apps.stream().filter(a -> a.getStatus() == Application.Status.PENDING).count();
        long passed = apps.stream().filter(a -> a.getStatus() == Application.Status.PASSED).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(UIHelper.createStatCard("Total Applications", String.valueOf(apps.size()), Theme.GRAY_800));
        stats.add(UIHelper.createStatCard("Pending Review", String.valueOf(pending), Theme.WARNING));
        stats.add(UIHelper.createStatCard("Accepted", String.valueOf(passed), Theme.SUCCESS));

        String[] cols = {"Position", "Course", "Applied Date", "Status", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Application app : apps) {
            model.addRow(new Object[]{
                    app.getPositionTitle(),
                    app.getCourseCode(),
                    app.getAppliedDate(),
                    app.getStatus().name(),
                    app.getFeedback() != null ? app.getFeedback() : ""
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
            if (old.isEmpty() || np.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!np.equals(cp)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User user = ds.getCurrentUser();
            if (user != null && ds.changePassword(user.getId(), old, np)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
