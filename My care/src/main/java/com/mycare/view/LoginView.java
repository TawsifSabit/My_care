package com.mycare.view;

import com.mycare.dao.UserDAO;
import com.mycare.model.User;
import com.mycare.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.net.URL;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public LoginView() {
        UIUtil.initModernTheme();
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("MyCare Hospital - Login");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.styleFrame(this);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon logoIcon = loadLogoIcon();
        if (logoIcon != null) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(logoLabel, gbc);
            gbc.gridy = 1;
            gbc.gridwidth = 1;
        } else {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel titleLabel = new JLabel("MyCare Hospital");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(titleLabel, gbc);
            gbc.gridy = 1;
            gbc.gridwidth = 1;
        }

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        UIUtil.styleTextField(usernameField);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        UIUtil.styleTextField(passwordField);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginButton = UIUtil.createPrimaryButton("Login");
        loginButton.addActionListener(new LoginActionListener());
        panel.add(loginButton, gbc);

        setContentPane(panel);
    }

    private ImageIcon loadLogoIcon() {
        String[] paths = {
            "src/main/java/com/mycare/view/login.png",
            "resources/login_logo.png",
            "login_logo.png",
            "login.png"
        };
        for (String path : paths) {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                Image scaled = icon.getImage().getScaledInstance(180, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        }
        return null;
    }

    private class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel() {
            ImageIcon icon = loadLogoIcon();
            backgroundImage = icon != null ? icon.getImage() : null;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                int width = getWidth();
                int height = getHeight();
                g.drawImage(backgroundImage, width - 130, height - 130, 110, 110, this);
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 220), 0, getHeight(), new Color(255, 255, 255, 200)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                User user = userDAO.authenticate(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginView.this, "Login successful!");
                    // Open main dashboard based on role
                    new MainDashboard(user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "Invalid credentials!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginView.this, "Database error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}