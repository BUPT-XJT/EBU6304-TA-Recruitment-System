import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Main extends JFrame {
    private User currentUser;
    private UserService userService = new UserService();
    private PositionService positionService = new PositionService();
    private ApplicationService applicationService = new ApplicationService();

    // 登录组件
    private JTextField tfUserId;
    private JPasswordField tfPassword;

    // 主界面组件
    private JFrame mainFrame;
    private JTextArea txtOutput;

    public Main() {
        // 初始化登录界面
        initLoginUI();
    }

    // 1. 登录界面
    private void initLoginUI() {
        setTitle("BUPT TA Recruitment System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel lblTitle = new JLabel("User Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(150, 10, 150, 30);
        panel.add(lblTitle);

        JLabel lblId = new JLabel("User ID:");
        lblId.setBounds(80, 60, 80, 25);
        panel.add(lblId);

        tfUserId = new JTextField();
        tfUserId.setBounds(150, 60, 150, 25);
        panel.add(tfUserId);

        JLabel lblPwd = new JLabel("Password:");
        lblPwd.setBounds(80, 100, 80, 25);
        panel.add(lblPwd);

        tfPassword = new JPasswordField();
        tfPassword.setBounds(150, 100, 150, 25);
        panel.add(tfPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 140, 100, 30);
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> login());
    }

    // 2. 登录逻辑
    private void login() {
        String id = tfUserId.getText().trim();
        String pwd = new String(tfPassword.getPassword()).trim();

        currentUser = userService.login(id, pwd);

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Login Failed!");
            return;
        }

        JOptionPane.showMessageDialog(this, "Login Success! Role: " + currentUser.getRole());
        this.dispose(); // 关闭登录窗口
        showMainUI();   // 打开主界面
    }

    // 3. 主界面（根据角色显示不同功能）
    private void showMainUI() {
        mainFrame = new JFrame("BUPT TA Recruitment System - Main");
        mainFrame.setSize(600, 500);
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        mainFrame.add(panel);

        // 顶部按钮栏
        JPanel btnPanel = new JPanel();
        panel.add(btnPanel, BorderLayout.NORTH);

        // 通用按钮
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> loadData());
        btnPanel.add(btnRefresh);

        // 根据角色添加功能按钮
        switch (currentUser.getRole()) {
            case "ADMIN":
                addAdminButtons(btnPanel);
                break;
            case "MO":
                addMOButtons(btnPanel);
                break;
            case "TA":
                addTAButtons(btnPanel);
                break;
        }

        // 内容显示区
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        panel.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
        loadData(); // 加载初始数据
    }

    // 4. 管理员功能：审核岗位
    private void addAdminButtons(JPanel panel) {
        JButton btnApprovePos = new JButton("Approve Position");
        btnApprovePos.addActionListener(e -> approvePosition());
        panel.add(btnApprovePos);
    }

    // 5. MO功能：审核TA申请
    private void addMOButtons(JPanel panel) {
        JButton btnReviewApp = new JButton("Review TA Application");
        btnReviewApp.addActionListener(e -> reviewApplication());
        panel.add(btnReviewApp);
    }

    // 6. TA功能：申请岗位
    private void addTAButtons(JPanel panel) {
        JButton btnApply = new JButton("Apply for Position");
        btnApply.addActionListener(e -> applyPosition());
        panel.add(btnApply);
    }

    // 7. 数据加载（基础展示）
    private void loadData() {
        txtOutput.setText("=== System Data ===\n");
        txtOutput.append("User: " + currentUser.getName() + " (" + currentUser.getRole() + ")\n\n");

        txtOutput.append("--- Positions ---\n");
        List<Position> positions = positionService.getAllPositions();
        for (Position p : positions) {
            txtOutput.append(p.getId() + " - " + p.getCourse() + " - " + p.getStatus() + "\n");
        }

        txtOutput.append("\n--- My Applications ---\n");
        List<Application> apps = applicationService.getApplicationsByUser(currentUser.getId());
        for (Application a : apps) {
            txtOutput.append(a.getId() + " - Pos: " + a.getPositionId() + " - " + a.getStatus() + "\n");
        }
    }

    // 8. 管理员审核岗位
    private void approvePosition() {
        String posId = JOptionPane.showInputDialog(mainFrame, "Enter Position ID to Approve:");
        if (posId == null || posId.isEmpty()) return;

        boolean success = positionService.updatePositionStatus(posId, "APPROVED");
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Position Approved!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed!");
        }
    }

    // 9. MO审核申请
    private void reviewApplication() {
        String appId = JOptionPane.showInputDialog(mainFrame, "Enter Application ID to Approve:");
        if (appId == null || appId.isEmpty()) return;

        boolean success = applicationService.updateApplicationStatus(appId, "APPROVED");
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Application Approved!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed!");
        }
    }

    // 10. TA申请岗位
    private void applyPosition() {
        String posId = JOptionPane.showInputDialog(mainFrame, "Enter Position ID to Apply:");
        if (posId == null || posId.isEmpty()) return;

        // 生成简单申请ID
        String appId = "APP" + (int)(Math.random() * 1000);
        Application newApp = new Application(appId, currentUser.getId(), posId, "PENDING", "None");

        boolean success = applicationService.createApplication(newApp);
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Application Submitted!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}