package com.bupt.ta;

import com.bupt.ta.ui.MainFrame;

import javax.swing.*;

/** Swing desktop UI entry point. Web server uses {@link Main}. */
public class DesktopMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
