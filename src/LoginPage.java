import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login page with username and password authentication.
 * Opens centered on screen with a clean dark UI.
 */
public class LoginPage extends JFrame {

    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "admin";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginPage() {
        setTitle("Shop Billing System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 500));
        initUI();
        LoggerUtil.getLogger().info("Login page displayed");
    }

    private void initUI() {
        // Full screen dark background
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(15, 23, 42));

        // Centered login card
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(30, 41, 59));
        cardPanel.setPreferredSize(new Dimension(420, 420));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 189, 248), 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(56, 189, 248));
        headerPanel.setPreferredSize(new Dimension(420, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("SHOP BILLING SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 41, 59));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridwidth = 1; gbc.weightx = 1.0;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(new Color(203, 213, 225));
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);

        gbc.gridy = 1;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2; gbc.insets = new Insets(14, 0, 6, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(new Color(203, 213, 225));
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(6, 0, 6, 0);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBackground(new Color(51, 65, 85));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(passwordField, gbc);

        // Login button - BIG and VISIBLE
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 6, 0);
        loginButton = new JButton("     LOGIN     ");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(56, 189, 248));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(14, 165, 233), 1),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(0, 48));
        loginButton.addActionListener(e -> authenticate());
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginButton.setBackground(new Color(14, 165, 233)); }
            public void mouseExited(MouseEvent e) { loginButton.setBackground(new Color(56, 189, 248)); }
        });
        formPanel.add(loginButton, gbc);

        // Status
        gbc.gridy = 5; gbc.insets = new Insets(8, 0, 0, 0);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(new Color(248, 113, 113));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);

        cardPanel.add(formPanel, BorderLayout.CENTER);

        // Footer in card
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(30, 41, 59));
        footerPanel.setPreferredSize(new Dimension(420, 30));
        JLabel footerLabel = new JLabel("Default credentials:  admin / admin");
        footerLabel.setForeground(new Color(100, 116, 139));
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerPanel.add(footerLabel);
        cardPanel.add(footerPanel, BorderLayout.SOUTH);

        outerPanel.add(cardPanel);

        // Enter key triggers
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) authenticate(); }
        });
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) passwordField.requestFocus(); }
        });

        setContentPane(outerPanel);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(new Color(51, 65, 85));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    private void authenticate() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        if (user.equals(VALID_USER) && pass.equals(VALID_PASS)) {
            LoggerUtil.getLogger().info("Login successful for user: " + user);
            dispose();
            SwingUtilities.invokeLater(() -> {
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
            });
        } else {
            LoggerUtil.getLogger().warning("Failed login attempt for user: " + user);
            statusLabel.setText("Invalid username or password!");
            passwordField.setText("");
        }
    }
}
