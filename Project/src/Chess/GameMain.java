package Chess;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GameMain {

    private JFrame frame;
    private JPanel panel2;
    private JFrame loginFrame;

    public static String whiteUsername;
    public static String blackUsername;

    // Database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chess";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    public GameMain() {
        frame = new JFrame();
        frame.setLayout(null);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.PINK);
        frame.setLocationRelativeTo(null);

        panel2 = new JPanel();
        panel2.setBackground(Color.GRAY);
        panel2.setBounds(1000, 0, 290, 720);
        frame.add(panel2);

        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.GRAY);
        panel1.setBounds(0, 0, 280, 720);
        frame.add(panel1);

        Board board = new Board(panel2, panel1);
        board.setBounds(280, 0, 720, 720);
        frame.add(board);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the login frame
        loginFrame = new JFrame();
        loginFrame.setUndecorated(true);
        loginFrame.setBackground(new Color(0, 0, 0,70)); // Make frame background transparent
        loginFrame.setLayout(null);
        loginFrame.setSize(700, 500);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add a semi-transparent panel for the login form
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(0, 0, 0, 0));
        loginPanel.setBounds(0, 0, 800, 600);
        Font myFont=new Font("Poppins", Font.BOLD,30);
        loginFrame.add(loginPanel);



        JLabel whiteUsernameLabel = new JLabel("Player 1 Username:");
        whiteUsernameLabel.setBounds(50, 110, 300, 50);
        whiteUsernameLabel.setFont(myFont);
        whiteUsernameLabel.setForeground(Color.WHITE);
        loginPanel.add(whiteUsernameLabel);

        JTextField whiteUsernameField = new JTextField();
        whiteUsernameField.setBounds(340, 110, 300, 50);
        whiteUsernameField.setFont(myFont);
        loginPanel.add(whiteUsernameField);

        JLabel whitePasswordLabel = new JLabel("Player 1 Password:");
        whitePasswordLabel.setBounds(50, 180, 300, 50);
        whitePasswordLabel.setForeground(Color.white);
        whitePasswordLabel.setFont(myFont);
        loginPanel.add(whitePasswordLabel);

        JPasswordField whitePasswordField = new JPasswordField();
        whitePasswordField.setBounds(340, 180, 300, 50);
        whitePasswordField.setFont(myFont);
        loginPanel.add(whitePasswordField);

        JLabel blackUsernameLabel = new JLabel("Player 2 Username:");
        blackUsernameLabel.setBounds(50, 250, 300, 50);
        blackUsernameLabel.setForeground(Color.white);
        blackUsernameLabel.setFont(myFont);
        loginPanel.add(blackUsernameLabel);

        JTextField blackUsernameField = new JTextField();
        blackUsernameField.setBounds(340, 250, 300, 50);
        blackUsernameField.setFont(myFont);
        loginPanel.add(blackUsernameField);

        JLabel blackPasswordLabel = new JLabel("Player 2 Password:");
        blackPasswordLabel.setBounds(50, 320, 350, 50);
        blackPasswordLabel.setForeground(Color.white);
        blackPasswordLabel.setFont(myFont);
        loginPanel.add(blackPasswordLabel);

        JPasswordField blackPasswordField = new JPasswordField();
        blackPasswordField.setBounds(340, 320, 300, 50);
        blackPasswordField.setFont(myFont);
        loginPanel.add(blackPasswordField);

        JButton loginButton = new JButton("START");
        loginButton.setBounds(270, 420, 150, 50);
        loginButton.setBackground(Color.gray);
        loginButton.setFont(myFont);
        loginPanel.add(loginButton);

        JButton close = new JButton("X");
        close.setFont(new Font("Poppins",Font.BOLD,30));
        close.setBounds(640, 0, 60, 60);
        close.setForeground(Color.RED);
        close.setBackground(Color.RED);
        close.setOpaque(false);
        close.setBorderPainted(true);
        close.setFocusPainted(false);
        loginPanel.add(close);

        close.addActionListener(e->{
            loginFrame.dispose();
        });

        loginButton.addActionListener(e -> {
            String whiteUsernameInput = whiteUsernameField.getText();
            String whitePasswordInput = new String(whitePasswordField.getPassword());
            String blackUsernameInput = blackUsernameField.getText();
            String blackPasswordInput = new String(blackPasswordField.getPassword());

            boolean whiteValid = validateCredentials(whiteUsernameInput, whitePasswordInput);
            boolean blackValid = validateCredentials(blackUsernameInput, blackPasswordInput);

            if (whiteValid && blackValid) {
                whiteUsername = whiteUsernameInput;
                blackUsername = blackUsernameInput;
                loginFrame.dispose();
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password for one or both players.");
            }
        });

        loginFrame.setVisible(true);
    }

    private boolean validateCredentials(String username, String password) {
        boolean isValid = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM players WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        isValid = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static String getWhiteUsername() {
        return whiteUsername;
    }

    public static String getBlackUsername() {
        return blackUsername;
    }

    public void showWindow() {
        showLoginFrame();
    }

    private void showLoginFrame() {
        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        GameMain game = new GameMain();
        game.showWindow();
    }
}
