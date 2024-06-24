package Stats_Window;

import Menus_Windows.BackgroundPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;



public class Stats {
    static final String DB_URL = "jdbc:mysql://localhost:3306/chess";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    private JFrame frame;
    private JTextField usernameField;
    private TransparentTextArea userDisplayArea;
    private TransparentTextArea matchesDisplayArea;

    public void createAndShowGUI() {
        frame = new JFrame("User Stats Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setLayout(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("D:/ACP/Project/assets/Options.png");
        backgroundPanel.setLayout(null);
        frame.setContentPane(backgroundPanel);

        Font headingFont = new Font("Poppins", Font.BOLD, 40);
        Font btnFont = new Font("Poppins", Font.BOLD, 20);

        JLabel heading = new JLabel("User Stats");

        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("BACK");

        searchButton.setBackground(new Color(0, 0, 0));
        backButton.setBackground(new Color(0, 0, 0));
        searchButton.setForeground(Color.white);
        backButton.setForeground(Color.white);

        searchButton.setFont(btnFont);
        backButton.setFont(btnFont);

        searchButton.setBorder(new LineBorder(Color.WHITE));
        backButton.setBorder(new LineBorder(Color.WHITE));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        userDisplayArea = new TransparentTextArea(new Color(0, 0, 0, 100));
        userDisplayArea.setEditable(false);
        userDisplayArea.setFont(new Font("Poppins", Font.PLAIN, 30));
        userDisplayArea.setForeground(Color.white);

        matchesDisplayArea = new TransparentTextArea(new Color(0, 0, 0, 100));
        matchesDisplayArea.setEditable(false);
        matchesDisplayArea.setFont(new Font("Poppins", Font.PLAIN, 25));
        matchesDisplayArea.setForeground(Color.white);

        usernameLabel.setFont(btnFont);
        usernameLabel.setForeground(Color.white);
        usernameField.setFont(btnFont);

        heading.setBounds(550, 50, 500, 75);
        heading.setFont(headingFont);
        heading.setForeground(Color.WHITE);

        usernameLabel.setBounds(100, 150, 150, 50);
        usernameField.setBounds(250, 150, 250, 50);
        searchButton.setBounds(250, 220, 150, 50);
        userDisplayArea.setBounds(50, 290, 400, 250);
        matchesDisplayArea.setBounds(500, 290, 750, 250);
        backButton.setBounds(580, 600, 150, 50);

        backgroundPanel.add(heading);
        backgroundPanel.add(usernameLabel);
        backgroundPanel.add(usernameField);
        backgroundPanel.add(searchButton);
        backgroundPanel.add(userDisplayArea);
        backgroundPanel.add(matchesDisplayArea);
        backgroundPanel.add(backButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                displayUserData(username);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    private void displayUserData(String username) {
        String userSql = "SELECT * FROM players WHERE username = ?";
        String matchesSql = "SELECT result, match_date, black_player, white_player FROM matches " +
                "WHERE white_player = ? OR black_player = ? " +
                "ORDER BY match_date DESC LIMIT 5";
        try (Connection con = getConnection();
             PreparedStatement userPs = con.prepareStatement(userSql)) {

            // Query for user data
            userPs.setString(1, username);
            try (ResultSet userResult = userPs.executeQuery()) {
                StringBuilder userData = new StringBuilder();
                int playerId = -1;
                if (userResult.next()) {
                    playerId = userResult.getInt("player_id");
                    userData.append("Name: ").append(userResult.getString("display_name")).append("\n");
                    userData.append("Matches Played: ").append(userResult.getInt("matches_played")).append("\n");
                    userData.append("Matches Won: ").append(userResult.getInt("matches_won")).append("\n");
                    userData.append("Matches Lost: ").append(userResult.getInt("matches_lost")).append("\n");
                    userData.append("Matches Drawn: ").append(userResult.getInt("matches_drawn")).append("\n");
                } else {
                    userData.append("User Not Found: ").append(username);
                }
                userDisplayArea.setText(userData.toString());

                if (playerId != -1) {
                    try (PreparedStatement matchesPs = con.prepareStatement(matchesSql)) {
                        matchesPs.setInt(1, playerId);
                        matchesPs.setInt(2, playerId);
                        try (ResultSet matchesResult = matchesPs.executeQuery()) {
                            StringBuilder matchesData = new StringBuilder("Last 5 Matches:\n");
                            while (matchesResult.next()) {
                                String role = matchesResult.getInt("black_player") == playerId ? "Black" : "White";
                                matchesData.append("Role: ").append(role)
                                        .append(", Result: ").append(matchesResult.getString("result"))
                                        .append(", Date: ").append(matchesResult.getTimestamp("match_date")).append("\n");
                            }
                            matchesDisplayArea.setText(matchesData.toString());
                        }
                    }
                } else {
                    matchesDisplayArea.setText("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Stats().createAndShowGUI();
            }
        });
    }
}
