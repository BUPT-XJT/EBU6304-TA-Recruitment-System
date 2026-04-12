package com.bupt.ta.ui.panel;

import com.bupt.ta.model.Application;
import com.bupt.ta.model.Position;
import com.bupt.ta.model.User;
import com.bupt.ta.service.DataService;
import com.bupt.ta.ui.MainFrame;
import com.bupt.ta.ui.Theme;
import com.bupt.ta.ui.component.Sidebar;
import com.bupt.ta.ui.component.UIHelper;
import com.bupt.ta.util.CvStorage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.Optional;

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
        sidebar.addMenuItem("D", "Dashboard");
        sidebar.addMenuItem("M", "My Profile");
        sidebar.addMenuItem("B", "Browse Positions");
        sidebar.addMenuItem("A", "My Applications");
        sidebar.addSection("Account");
        sidebar.addMenuItem("S", "Settings");
        sidebar.addMenuItem("<", "Logout");
        sidebar.addUserSection(userName, initials);
        sidebar.setActive("Dashboard");

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(Theme.WINDOW_BG);

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
        panel.setBackground(Theme.WINDOW_BG);
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
        panel.setBackground(Theme.WINDOW_BG);
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

        JPanel grid = new JPanel(new GridLayout(1, 2, 28, 0));
        grid.setOpaque(false);

        // Left card - personal info
        JPanel leftCard = UIHelper.createCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        JLabel leftHead = UIHelper.createLabel("Personal Information", Theme.FONT_H3, Theme.GRAY_700);
        leftHead.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCard.add(leftHead);
        leftCard.add(Box.createVerticalStrut(14));

        JTextField nameField = UIHelper.createTextField("Full Name");
        if (user != null) nameField.setText(user.getName());

        JTextField progField = UIHelper.createTextField("Programme");
        if (user != null && user.getProgramme() != null) progField.setText(user.getProgramme());

        JTextField yearField = UIHelper.createTextField("Year of Study");
        if (user != null && user.getYearOfStudy() != null) yearField.setText(user.getYearOfStudy());

        JTextField skillsField = UIHelper.createTextField("Skills (comma separated)");
        if (user != null && user.getSkills() != null) skillsField.setText(String.join(", ", user.getSkills()));

        JTextArea expArea = UIHelper.createTextArea("Previous TA experience", 4);
        if (user != null && user.getExperience() != null) expArea.setText(user.getExperience());
        expArea.setBackground(Color.WHITE);
        JScrollPane expScroll = new JScrollPane(expArea);
        expScroll.setBorder(null);
        expScroll.getViewport().setBackground(Color.WHITE);
        expScroll.setPreferredSize(new Dimension(200, 108));
        expScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        leftCard.add(UIHelper.createFormRow("Full Name", nameField));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(UIHelper.createFormRow("Programme", progField));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(UIHelper.createFormRow("Year of Study", yearField));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(UIHelper.createFormRow("Skills", skillsField));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(UIHelper.createFormRow("Previous TA Experience", expScroll));

        // Right card - CV & education
        JPanel rightCard = UIHelper.createCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        JLabel rightHeadCv = UIHelper.createLabel("Upload CV / Resume", Theme.FONT_H3, Theme.GRAY_700);
        rightHeadCv.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(rightHeadCv);
        rightCard.add(Box.createVerticalStrut(14));

        JLabel uploadText = new JLabel("Drag & drop your CV here, or browse");
        uploadText.setFont(Theme.FONT_BODY);
        uploadText.setForeground(Theme.GRAY_600);
        uploadText.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel uploadHint = new JLabel("PDF, DOC, DOCX — max 10 MB");
        uploadHint.setFont(Theme.FONT_SMALL);
        uploadHint.setForeground(Theme.GRAY_400);
        uploadHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel uploadBox = createProfileCvDropZone(uploadText, uploadHint);
        uploadBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JButton browseBtn = UIHelper.createButton("Choose File", Theme.PRIMARY, Color.WHITE);
        browseBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea cvStatus = new JTextArea(2, 42);
        cvStatus.setEditable(false);
        cvStatus.setOpaque(false);
        cvStatus.setLineWrap(true);
        cvStatus.setWrapStyleWord(true);
        cvStatus.setFont(Theme.FONT_BODY);
        cvStatus.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        cvStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshCvStatusLabel(user, cvStatus);

        new DropTarget(uploadBox, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable tr = dtde.getTransferable();
                    if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
                        if (files != null && !files.isEmpty()) {
                            tryUploadCv(user, files.get(0), cvStatus);
                        }
                    }
                    dtde.dropComplete(true);
                } catch (Exception ex) {
                    dtde.dropComplete(false);
                }
            }
        });

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tryUploadCv(user, chooser.getSelectedFile(), cvStatus);
            }
        });

        rightCard.add(uploadBox);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(browseBtn);
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(cvStatus);
        rightCard.add(Box.createVerticalStrut(20));

        JLabel rightHeadEd = UIHelper.createLabel("Education Background", Theme.FONT_H3, Theme.GRAY_700);
        rightHeadEd.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(rightHeadEd);
        rightCard.add(Box.createVerticalStrut(12));

        JTextField uniField = UIHelper.createTextField("University");
        if (user != null && user.getUniversity() != null) uniField.setText(user.getUniversity());

        JTextField gpaField = UIHelper.createTextField("GPA");
        if (user != null && user.getGpa() != null) gpaField.setText(user.getGpa());

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

    /** Rounded dashed drop zone with a simple vector “document” icon (no emoji). */
    private JPanel createProfileCvDropZone(JLabel line1, JLabel line2) {
        JPanel surface = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Theme.GRAY_100);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
                float[] dash = {6f, 5f};
                g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0));
                g2.setColor(Theme.GRAY_300);
                g2.drawRoundRect(1, 1, w - 3, h - 3, 17, 17);
                g2.dispose();
            }
        };
        surface.setOpaque(false);
        surface.setLayout(new BoxLayout(surface, BoxLayout.Y_AXIS));

        JPanel art = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int x0 = 6;
                int y0 = 4;
                int pw = 36;
                int ph = 44;
                g2.setColor(Theme.PRIMARY_LIGHT);
                g2.fillRoundRect(x0, y0, pw, ph, 10, 10);
                g2.setColor(new Color(191, 219, 254));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(x0, y0, pw, ph, 10, 10);
                g2.setColor(Theme.PRIMARY);
                g2.drawLine(x0 + 10, y0 + 18, x0 + pw - 10, y0 + 18);
                g2.drawLine(x0 + 10, y0 + 26, x0 + pw - 14, y0 + 26);
                int fold = 10;
                g2.setColor(Color.WHITE);
                g2.fillPolygon(
                        new int[]{x0 + pw - fold, x0 + pw, x0 + pw},
                        new int[]{y0, y0, y0 + fold},
                        3);
                g2.setColor(Theme.PRIMARY);
                g2.drawLine(x0 + pw - fold, y0, x0 + pw, y0 + fold);
                g2.dispose();
            }
        };
        art.setOpaque(false);
        art.setPreferredSize(new Dimension(48, 56));
        art.setMaximumSize(new Dimension(48, 56));
        art.setAlignmentX(Component.CENTER_ALIGNMENT);

        surface.add(Box.createVerticalStrut(8));
        surface.add(art);
        surface.add(Box.createVerticalStrut(10));
        surface.add(line1);
        surface.add(Box.createVerticalStrut(4));
        surface.add(line2);
        surface.add(Box.createVerticalStrut(12));
        return surface;
    }

    private JPanel createBrowsePositions() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.WINDOW_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout(24, 0));
        top.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.createLabel("Available TA Positions", Theme.FONT_TITLE, Theme.GRAY_800));
        titleBox.add(UIHelper.createLabel("Browse and apply for Teaching Assistant roles", Theme.FONT_SUBTITLE, Theme.GRAY_500));

        JTextField searchField = UIHelper.createTextField("Search by course name, skills, or keywords...");
        searchField.setPreferredSize(new Dimension(360, 42));
        JPanel searchWrap = new JPanel();
        searchWrap.setOpaque(false);
        searchWrap.setLayout(new BoxLayout(searchWrap, BoxLayout.Y_AXIS));
        searchWrap.add(searchField);
        searchWrap.add(Box.createVerticalStrut(4));
        JLabel searchHint = UIHelper.createLabel("Filters as you type", Theme.FONT_SMALL, Theme.GRAY_500);
        searchHint.setAlignmentX(Component.RIGHT_ALIGNMENT);
        searchWrap.add(searchHint);

        top.add(titleBox, BorderLayout.WEST);
        top.add(searchWrap, BorderLayout.EAST);

        List<Position> positions = ds.getApprovedPositions();
        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 24));
        grid.setOpaque(false);

        Runnable refilterPositions = () -> {
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
        };

        for (Position pos : positions) {
            grid.add(createPositionCard(pos));
        }

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void schedule() {
                SwingUtilities.invokeLater(refilterPositions);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                schedule();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                schedule();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                schedule();
            }
        });
        searchField.addActionListener(e -> refilterPositions.run());

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.WINDOW_BG);
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
        JLabel title = UIHelper.createLabel(pos.getCourseName() + " TA", Theme.FONT_H3, Theme.GRAY_800);
        JComponent badge;
        if (pos.getStatus() == Position.Status.APPROVED) {
            badge = UIHelper.createBadge("Open", Theme.SUCCESS_LIGHT, Theme.SUCCESS);
        } else {
            badge = UIHelper.createBadge("Closed", Theme.GRAY_200, Theme.GRAY_500);
        }
        topRow.add(title, BorderLayout.WEST);
        topRow.add(UIHelper.alignTopTrailing(badge), BorderLayout.EAST);

        JLabel dept = UIHelper.createLabel(pos.getCourseCode() + " - " + pos.getDepartment(), Theme.FONT_SMALL, Theme.GRAY_500);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        info.setOpaque(false);
        info.add(UIHelper.createTag(pos.getNumPositions() + " openings"));
        info.add(UIHelper.createTag(pos.getHoursPerWeek() == 0 ? "Flexible hours" : pos.getHoursPerWeek() + " hrs/wk"));
        info.add(UIHelper.createTag(pos.getPayRate()));

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        tagsPanel.setOpaque(false);
        for (String skill : pos.getRequiredSkills()) {
            tagsPanel.add(UIHelper.createTag(skill));
        }

        JLabel deadline = UIHelper.createLabel("Deadline: " + pos.getDeadline(), Theme.FONT_SMALL, Theme.GRAY_500);
        deadline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton applyBtn = UIHelper.createButton("View & Apply", Theme.PRIMARY, Color.WHITE);
        applyBtn.setFont(Theme.FONT_BODY_BOLD);
        applyBtn.addActionListener(e -> showPositionDetail(pos));
        applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel footer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Theme.CARD_FOOTER_BG);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);
                g2.setColor(Theme.CARD_FOOTER_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 2, h - 2, 16, 16);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        footer.add(deadline);
        footer.add(Box.createVerticalStrut(10));
        footer.add(applyBtn);

        card.add(topRow);
        card.add(Box.createVerticalStrut(6));
        card.add(dept);
        card.add(Box.createVerticalStrut(10));
        card.add(info);
        card.add(Box.createVerticalStrut(8));
        card.add(tagsPanel);
        card.add(Box.createVerticalStrut(14));
        card.add(footer);

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

        if (user != null && user.hasCvOnDisk()) {
            String name = user.getCvFileName() != null && !user.getCvFileName().isEmpty()
                    ? user.getCvFileName() : user.getCvStoragePath();
            JLabel cvLabel = UIHelper.createLabel("\uD83D\uDCC4 " + name, Theme.FONT_BODY, Theme.GRAY_600);
            rightPanel.add(cvLabel);
            rightPanel.add(UIHelper.createLabel("Stored under data/" + user.getCvStoragePath(), Theme.FONT_SMALL, Theme.GRAY_400));
        } else {
            rightPanel.add(UIHelper.createLabel("No CV on file. Upload in My Profile (PDF/DOC/DOCX, max 10MB).", Theme.FONT_BODY, Theme.DANGER));
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
                if (!user.hasCvOnDisk()) {
                    JOptionPane.showMessageDialog(detailPanel,
                            "Please upload your CV in My Profile. The file must be saved under data/cvs before you can apply.",
                            "CV required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
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

    private void refreshCvStatusLabel(User user, JTextArea cvStatus) {
        if (user != null && user.hasCvOnDisk()) {
            String n = user.getCvFileName() != null && !user.getCvFileName().isEmpty()
                    ? user.getCvFileName() : user.getCvStoragePath();
            cvStatus.setText("Saved: " + n + "\nLocation: data/" + user.getCvStoragePath());
            cvStatus.setForeground(Theme.SUCCESS);
            cvStatus.setDisabledTextColor(Theme.SUCCESS);
            cvStatus.setCaretPosition(0);
        } else if (user != null && user.getCvFileName() != null && !user.getCvFileName().isEmpty()) {
            cvStatus.setText("CV name is on file, but the file is missing. Please choose a file and upload again.");
            cvStatus.setForeground(Theme.WARNING);
            cvStatus.setDisabledTextColor(Theme.WARNING);
            cvStatus.setCaretPosition(0);
        } else {
            cvStatus.setText("");
            cvStatus.setForeground(Theme.GRAY_500);
            cvStatus.setDisabledTextColor(Theme.GRAY_500);
        }
    }

    private void tryUploadCv(User user, File file, JTextArea cvStatus) {
        if (user == null || file == null) {
            return;
        }
        Optional<String> stored = CvStorage.storeCv(user.getId(), file, user.getCvStoragePath());
        if (stored.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the CV. Use PDF or Word (.doc/.docx), non-empty file, max 10 MB.",
                    "CV upload failed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        user.setCvFileName(file.getName());
        user.setCvStoragePath(stored.get());
        ds.updateUser(user);
        refreshCvStatusLabel(user, cvStatus);
    }

    private JPanel createMiniStat(String label, String value) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.GRAY_100);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.CORNER_RADIUS, Theme.CORNER_RADIUS);
                g2.setColor(Theme.GRAY_200);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.CORNER_RADIUS, Theme.CORNER_RADIUS);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
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
        panel.setBackground(Theme.WINDOW_BG);
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
