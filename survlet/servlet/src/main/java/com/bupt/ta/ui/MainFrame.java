package com.bupt.ta.ui;

import com.bupt.ta.model.User;
import com.bupt.ta.service.DataService;
import com.bupt.ta.ui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final DataService dataService;
    private final LoginPanel loginPanel;

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String TA_MAIN = "TA_MAIN";
    public static final String MO_MAIN = "MO_MAIN";
    public static final String ADMIN_MAIN = "ADMIN_MAIN";

    public MainFrame() {
        dataService = DataService.getInstance();
        setTitle("BUPT TA Recruitment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Theme.WINDOW_BG);

        loginPanel = new LoginPanel(this);
        contentPanel.add(loginPanel, LOGIN);
        contentPanel.add(new RegisterPanel(this), REGISTER);
        contentPanel.add(new TAMainPanel(this), TA_MAIN);
        contentPanel.add(new MOMainPanel(this), MO_MAIN);
        contentPanel.add(new AdminMainPanel(this), ADMIN_MAIN);

        add(contentPanel);
        showPanel(LOGIN);
    }

    public void showPanel(String name) {
        if (name.equals(TA_MAIN)) {
            contentPanel.remove(getComponentByName(TA_MAIN));
            contentPanel.add(new TAMainPanel(this), TA_MAIN);
        } else if (name.equals(MO_MAIN)) {
            contentPanel.remove(getComponentByName(MO_MAIN));
            contentPanel.add(new MOMainPanel(this), MO_MAIN);
        } else if (name.equals(ADMIN_MAIN)) {
            contentPanel.remove(getComponentByName(ADMIN_MAIN));
            contentPanel.add(new AdminMainPanel(this), ADMIN_MAIN);
        }
        cardLayout.show(contentPanel, name);
    }

    private Component getComponentByName(String name) {
        for (Component c : contentPanel.getComponents()) {
            if (name.equals(c.getName())) return c;
        }
        return contentPanel.getComponent(0);
    }

    public void loginSuccess(User user) {
        dataService.setCurrentUser(user);
        switch (user.getRole()) {
            case TA:
                showPanel(TA_MAIN);
                break;
            case MO:
                showPanel(MO_MAIN);
                break;
            case ADMIN:
                showPanel(ADMIN_MAIN);
                break;
        }
    }

    public void logout() {
        dataService.setCurrentUser(null);
        loginPanel.clearForm();
        showPanel(LOGIN);
    }

    public DataService getDataService() {
        return dataService;
    }
}
