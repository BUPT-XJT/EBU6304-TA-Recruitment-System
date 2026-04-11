package com.bupt.ta.ui.panel;

import com.bupt.ta.model.User;
import com.bupt.ta.service.DataService;
import com.bupt.ta.ui.MainFrame;
import com.bupt.ta.ui.Theme;
import com.bupt.ta.ui.component.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RegisterPanel extends JPanel {
    private final MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(600, 580));

        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.GRAY_800);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel step = new JLabel("Account Information");
        step.setFont(Theme.FONT_BODY);
        step.setForeground(Theme.GRAY_400);
        step.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(50);
        progress.setStringPainted(false);
        progress.setForeground(Theme.PRIMARY);
        progress.setBackground(Theme.GRAY_200);
        progress.setMaximumSize(new Dimension(300, 6));
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        progress.setBorderPainted(false);

        JTextField nameField = UIHelper.createTextField("Enter your full name");
        JTextField studentIdField = UIHelper.createTextField("e.g., 2024213000");
        JTextField emailField = UIHelper.createTextField("your.email@bupt.edu.cn");
        JTextField phoneField = UIHelper.createTextField("+86 xxx xxxx xxxx");
        JPasswordField passwordField = UIHelper.createPasswordField("Min. 8 characters");
        JPasswordField confirmField = UIHelper.createPasswordField("Re-enter your password");

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 38);
        nameField.setMaximumSize(fieldSize);
        studentIdField.setMaximumSize(fieldSize);
        emailField.setMaximumSize(fieldSize);
        phoneField.setMaximumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        confirmField.setMaximumSize(fieldSize);

        JPanel row1 = createRow(
                UIHelper.createFormRow("Full Name *", nameField),
                UIHelper.createFormRow("Student ID *", studentIdField));
        JPanel row2 = createRow(
                UIHelper.createFormRow("Email Address *", emailField),
                UIHelper.createFormRow("Phone Number", phoneField));
        JPanel row3 = createRow(
                UIHelper.createFormRow("Password *", passwordField),
                UIHelper.createFormRow("Confirm Password *", confirmField));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(600, 44));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = UIHelper.createButton("\u2190 Back to Login", Theme.GRAY_100, Theme.GRAY_600);
        backBtn.addActionListener(e -> mainFrame.showPanel(MainFrame.LOGIN));

        JButton registerBtn = UIHelper.createButton("Create Account", Theme.PRIMARY, Color.WHITE);
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String sid = studentIdField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (name.isEmpty() || sid.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pass.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setName(name);
            user.setStudentId(sid);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(pass);
            user.setRole(User.Role.TA);
            user.setSkills(Arrays.asList());
            user.setUniversity("Beijing University of Posts and Telecom");

            if (DataService.getInstance().registerTA(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel(MainFrame.LOGIN);
            } else {
                JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(backBtn);
        btnRow.add(registerBtn);

        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(step);
        form.add(Box.createVerticalStrut(10));
        form.add(progress);
        form.add(Box.createVerticalStrut(24));
        form.add(row1);
        form.add(Box.createVerticalStrut(12));
        form.add(row2);
        form.add(Box.createVerticalStrut(12));
        form.add(row3);
        form.add(Box.createVerticalStrut(24));
        form.add(btnRow);

        add(form);
    }

    private JPanel createRow(JPanel left, JPanel right) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(600, 70));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(left);
        row.add(right);
        return row;
    }
}
