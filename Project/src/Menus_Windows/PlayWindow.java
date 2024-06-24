package Menus_Windows;

import Chess.GameMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayWindow extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chess";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public PlayWindow() {
        JFrame registerFrame = new JFrame();
        registerFrame.setUndecorated(true);
        registerFrame.setBackground(new Color(255, 255, 255, 10));
        registerFrame.setLayout(null);
        registerFrame.setSize(700, 500);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBackground(new Color(0, 0, 0, 0));
        registerPanel.setBounds(0, 0, 800, 600);
        Font myFont = new Font("Poppins", Font.BOLD, 30);
        registerFrame.add(registerPanel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 110, 300, 50);
        usernameLabel.setFont(myFont);
        usernameLabel.setForeground(Color.WHITE);
        registerPanel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(340, 110, 300, 50);
        usernameField.setFont(myFont);
        registerPanel.add(usernameField);

        JLabel userPasswordLabel = new JLabel("Password:");
        userPasswordLabel.setBounds(50, 180, 300, 50);
        userPasswordLabel.setForeground(Color.white);
        userPasswordLabel.setFont(myFont);
        registerPanel.add(userPasswordLabel);

        JPasswordField userPasswordField = new JPasswordField();
        userPasswordField.setBounds(340, 180, 300, 50);
        userPasswordField.setFont(myFont);
        registerPanel.add(userPasswordField);

        JLabel displayNameLabel = new JLabel("Display Name:");
        displayNameLabel.setBounds(50, 250, 350, 50);
        displayNameLabel.setForeground(Color.white);
        displayNameLabel.setFont(myFont);
        registerPanel.add(displayNameLabel);

        JTextField displayNameField = new JTextField();
        displayNameField.setBounds(340, 250, 300, 50);
        displayNameField.setFont(myFont);
        registerPanel.add(displayNameField);

        JButton rButton = new JButton("Register");
        rButton.setBounds(250, 350, 180, 50);
        rButton.setBackground(Color.gray);
        rButton.setFont(myFont);
        registerPanel.add(rButton);

        JButton close = new JButton("X");
        close.setFont(new Font("Poppins",Font.BOLD,30));
        close.setBounds(640, 0, 60, 60);
        close.setForeground(Color.RED);
        close.setBackground(Color.RED);
        close.setOpaque(false);
        close.setBorderPainted(true);
        close.setFocusPainted(false);
        registerPanel.add(close);

        rButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(userPasswordField.getPassword());
                String displayName = displayNameField.getText();
                registerUser(username, password, displayName);
            }
        });

        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            registerFrame.dispose();
            }
        });

        setTitle("Play");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String imagePath = "D:/ACP/Project/assets/Options.png";
        BackgroundPanel backgroundPanel = new BackgroundPanel(imagePath);
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton playButton = createButton("Play");
        JButton registerButton = createButton("Register");
        JButton exitButton = createButton("Back");

        playButton.addActionListener(e -> {
            GameMain g = new GameMain();
            g.showWindow();
        });

        registerButton.addActionListener(e -> {
            registerFrame.setVisible(true);
        });

        exitButton.addActionListener(e -> {
            dispose();
        });

        backgroundPanel.add(playButton, gbc);
        backgroundPanel.add(registerButton, gbc);
        backgroundPanel.add(exitButton, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 34));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(222, 22, 213, 0));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        return button;
    }

    private void registerUser(String username, String password, String displayName) {
        String sql = "INSERT INTO players (username, password, display_name, matches_played, matches_won, matches_lost, matches_drawn) " +
                "VALUES (?, ?, ?, 0, 0, 0, 0)";
        try (Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, displayName);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayWindow playWindow = new PlayWindow();
            playWindow.setVisible(true);
        });
    }
}
