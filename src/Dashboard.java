import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Dashboard - Full screen central navigation.
 * Large clickable cards for each module.
 */
public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Shop Billing System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        initUI();
        LoggerUtil.getLogger().info("Dashboard displayed");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(15, 23, 42));

        JLabel titleLabel = new JLabel("SHOP BILLING SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(new Color(56, 189, 248));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage your shop operations - Products, Billing, Sales");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Navigation cards - 2x2 grid with big cards
        JPanel navPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        navPanel.setBackground(new Color(15, 23, 42));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        navPanel.add(createNavCard("PRODUCT MANAGEMENT",
            "Add new products, edit prices, manage stock",
            new Color(16, 185, 129), e -> openProductManagement()));

        navPanel.add(createNavCard("BILLING SYSTEM",
            "Create bills, select items, enter customer name",
            new Color(56, 189, 248), e -> openBilling()));

        navPanel.add(createNavCard("SALES RECORDS",
            "View all bills, customer history, daily sales",
            new Color(251, 191, 36), e -> openSalesReport()));

        navPanel.add(createNavCard("LOGOUT / EXIT",
            "Sign out from the system",
            new Color(248, 113, 113), e -> logout()));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(15, 23, 42));
        footerPanel.setPreferredSize(new Dimension(0, 40));
        JLabel footerLabel = new JLabel("Shop Billing System v1.0  |  GST Rate: 18%");
        footerLabel.setForeground(new Color(71, 85, 105));
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createNavCard(String title, String desc, Color accentColor, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thick accent line at top
        JPanel accentLine = new JPanel();
        accentLine.setBackground(accentColor);
        accentLine.setPreferredSize(new Dimension(0, 5));
        card.add(accentLine, BorderLayout.NORTH);

        // Content centered
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(30, 41, 59));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(30, 41, 59));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(accentColor);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLbl.setForeground(new Color(148, 163, 184));
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(descLbl);
        contentPanel.add(textPanel);
        card.add(contentPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(51, 65, 85));
                contentPanel.setBackground(new Color(51, 65, 85));
                textPanel.setBackground(new Color(51, 65, 85));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(30, 41, 59));
                contentPanel.setBackground(new Color(30, 41, 59));
                textPanel.setBackground(new Color(30, 41, 59));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }
        });

        return card;
    }

    private void openProductManagement() {
        LoggerUtil.getLogger().info("Opening Product Management");
        new ProductManagement(this).setVisible(true);
    }

    private void openBilling() {
        LoggerUtil.getLogger().info("Opening Billing System");
        new BillingPage(this).setVisible(true);
    }

    private void openSalesReport() {
        LoggerUtil.getLogger().info("Opening Sales Records");
        new SalesReport(this).setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Confirm Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            LoggerUtil.getLogger().info("User logged out");
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginPage login = new LoginPage();
                login.setVisible(true);
            });
        }
    }
}
