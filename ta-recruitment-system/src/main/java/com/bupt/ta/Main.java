package com.bupt.ta;

import com.bupt.ta.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("control", new Color(249, 250, 251));
                    UIManager.put("nimbusBase", new Color(37, 99, 235));
                    UIManager.put("nimbusBlueGrey", new Color(229, 231, 235));
                    UIManager.put("nimbusFocus", new Color(37, 99, 235));
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
