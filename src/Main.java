import javax.swing.*;

/**
 * Main entry point for the Shop Billing System application.
 * Initializes the logger and displays the login screen.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize logger
        LoggerUtil.init();
        LoggerUtil.getLogger().info("Application starting...");

        // Use Metal (Cross-Platform) L&F for reliable button colors on dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            LoggerUtil.getLogger().warning("Cross-platform L&F not available, using default");
        }

        // Global UI customizations for dark theme
        UIManager.put("OptionPane.background", new java.awt.Color(30, 41, 59));
        UIManager.put("Panel.background", new java.awt.Color(30, 41, 59));
        UIManager.put("OptionPane.messageForeground", java.awt.Color.WHITE);

        // Launch login page on EDT
        SwingUtilities.invokeLater(() -> {
            DBConnection.initializeDatabase();
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}
