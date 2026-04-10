import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Main Entry for the BUPT TA Recruitment System.
 * Fully English UI with optimized GridBagLayout for the Login screen.
 */
public class Main extends JFrame {
    private User currentUser;
    private final UserService userService = new UserService();
    private final PositionService positionService = new PositionService();
    private final ApplicationService applicationService = new ApplicationService();

    // Core UI Components
    private JTable tblPositions;
    private JTable tblApplications;
    private DefaultTableModel modelPositions;
    private DefaultTableModel modelApplications;

    public Main() {
        // Set System Look and Feel for a modern native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        initLoginUI();
    }

    // ==========================================
    // 1. Login Interface (Optimized Layout)
    // ==========================================
    private void initLoginUI() {
        setTitle("BUPT TA Recruitment System - Login");
        setSize(400, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Using GridBagLayout to prevent "blank" or overlapping components
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Header
        JLabel lblTitle = new JLabel("System Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // User ID
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("User ID:"), gbc);
        JTextField tfUser = new JTextField(15);
        gbc.gridx = 1;
        panel.add(tfUser, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField tfPass = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(tfPass, gbc);

        // Confirm Button (Fixed the blank issue with explicit colors)
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setBackground(new Color(0, 51, 102));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(100, 40));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(btnConfirm, gbc);

        add(panel);

        // Login Logic
        btnConfirm.addActionListener(e -> {
            String id = tfUser.getText().trim();
            String pwd = new String(tfPass.getPassword()).trim();
            currentUser = userService.login(id, pwd);

            if (currentUser != null) {
                this.dispose();
                showMainUI();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid ID or Password. Please try again!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ==========================================
    // 2. Main Dashboard
    // ==========================================
    private void showMainUI() {
        JFrame frame = new JFrame("TA Management System - Welcome, " + currentUser.getName());
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // --- Left Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(235, 240, 245));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblRole = new JLabel("Role: " + currentUser.getRole());
        lblRole.setFont(new Font("Arial", Font.BOLD, 14));
        sidebar.add(lblRole);
        sidebar.add(Box.createVerticalStrut(30));

        // Navigation Buttons
        JButton btnRefresh = createNavButton("Refresh Data");
        btnRefresh.addActionListener(e -> loadDataToTables());
        sidebar.add(btnRefresh);

        String role = currentUser.getRole().toUpperCase();
        if ("TA".equals(role)) {
            JButton btnApply = createNavButton("Apply Position");
            btnApply.addActionListener(e -> applyPosition(frame));
            sidebar.add(btnApply);
        } else if ("MO".equals(role)) {
            JButton btnReview = createNavButton("Review App");
            btnReview.addActionListener(e -> reviewApplication(frame));
            sidebar.add(btnReview);
        } else if ("ADMIN".equals(role)) {
            JButton btnApprove = createNavButton("Approve Position");
            btnApprove.addActionListener(e -> approvePosition(frame));
            sidebar.add(btnApprove);
        }

        // --- Content Area (Tables) ---
        JPanel content = new JPanel(new GridLayout(2, 1, 0, 20));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Position Table
        modelPositions = new DefaultTableModel(new String[]{"Pos ID", "Manager", "Course", "Status"}, 0);
        tblPositions = new JTable(modelPositions);
        JPanel pnlPos = new JPanel(new BorderLayout());
        pnlPos.add(new JLabel("Open Positions"), BorderLayout.NORTH);
        pnlPos.add(new JScrollPane(tblPositions), BorderLayout.CENTER);
        content.add(pnlPos);

        // 2. Application Table
        modelApplications = new DefaultTableModel(new String[]{"App ID", "TA ID", "Pos ID", "Status", "Feedback"}, 0);
        tblApplications = new JTable(modelApplications);
        JPanel pnlApp = new JPanel(new BorderLayout());
        pnlApp.add(new JLabel("My Applications / Records"), BorderLayout.NORTH);
        pnlApp.add(new JScrollPane(tblApplications), BorderLayout.CENTER);
        content.add(pnlApp);

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(content, BorderLayout.CENTER);

        loadDataToTables();
        frame.setVisible(true);
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(180, 40));
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        return b;
    }

    // ==========================================
    // 3. Logic & Service Calls
    // ==========================================
    private void loadDataToTables() {
        modelPositions.setRowCount(0);
        modelApplications.setRowCount(0);

        for (Position p : positionService.getAllPositions()) {
            modelPositions.addRow(new Object[]{p.getId(), p.getMoId(), p.getCourse(), p.getStatus()});
        }

        List<Application> apps;
        if ("TA".equalsIgnoreCase(currentUser.getRole())) {
            apps = applicationService.getApplicationsByUser(currentUser.getId());
        } else {
            apps = applicationService.getAllApplications();
        }

        for (Application a : apps) {
            modelApplications.addRow(new Object[]{a.getId(), a.getTaId(), a.getPositionId(), a.getStatus(), a.getFeedback()});
        }
    }

    private void applyPosition(JFrame parent) {
        String posId = JOptionPane.showInputDialog(parent, "Enter the Position ID you wish to apply for:");
        if (posId == null || posId.trim().isEmpty()) return;

        String appId = "APP" + (System.currentTimeMillis() % 100000);
        Application newApp = new Application(appId, currentUser.getId(), posId.trim(), "PENDING", "None");

        if (applicationService.createApplication(newApp)) {
            JOptionPane.showMessageDialog(parent, "Application submitted successfully!");
            loadDataToTables();
        }
    }

    private void reviewApplication(JFrame parent) {
        String appId = JOptionPane.showInputDialog(parent, "Enter Application ID to approve:");
        if (appId != null && !appId.trim().isEmpty()) {
            if (applicationService.updateApplicationStatus(appId.trim(), "APPROVED")) {
                JOptionPane.showMessageDialog(parent, "Application Approved!");
                loadDataToTables();
            }
        }
    }

    private void approvePosition(JFrame parent) {
        String posId = JOptionPane.showInputDialog(parent, "Enter Position ID to verify:");
        if (posId != null && !posId.trim().isEmpty()) {
            if (positionService.updatePositionStatus(posId.trim(), "APPROVED")) {
                JOptionPane.showMessageDialog(parent, "Position Verified!");
                loadDataToTables();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}