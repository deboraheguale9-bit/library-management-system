package util;

import swingui.LoginWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Start the application on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginWindow();
                System.out.println("✅ Library Management System Started");
            } catch (Exception e) {
                System.err.println("❌ Failed to start application:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start application:\n" + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
