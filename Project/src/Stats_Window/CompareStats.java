package Stats_Window;

import Menus_Windows.BackgroundPanel;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CompareStats extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/chess";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    private JTextField usernameField1;
    private JTextField usernameField2;
    private TransparentTextArea comparisonResultArea;
    private TransparentTextArea matchesDisplayArea;

    public CompareStats() {
        setTitle("Compare Stats");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String imagePath = "D:/ACP/Project/assets/Options.png";
        BackgroundPanel backgroundPanel = new BackgroundPanel(imagePath);
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(null);

        Font btnFont = new Font("Poppins", Font.BOLD, 20);

        JLabel heading = new JLabel("Compare User Stats");
        heading.setFont(new Font("Poppins", Font.BOLD, 40));
        heading.setForeground(Color.WHITE);
        heading.setBounds(500, 50, 500, 75);
        backgroundPanel.add(heading);

        JLabel usernameLabel1 = new JLabel("Username 1:");
        usernameField1 = new JTextField();
        JLabel usernameLabel2 = new JLabel("Username 2:");
        usernameField2 = new JTextField();
        JButton compareButton = new JButton("Compare");
        JButton backButton = new JButton("Back");

        comparisonResultArea = new TransparentTextArea(new Color(0, 0, 0, 100));
        comparisonResultArea.setEditable(false);
        comparisonResultArea.setFont(new Font("Poppins", Font.PLAIN, 25));
        comparisonResultArea.setForeground(Color.white);

        matchesDisplayArea = new TransparentTextArea(new Color(0, 0, 0, 100));
        matchesDisplayArea.setEditable(false);
        matchesDisplayArea.setFont(new Font("Poppins", Font.PLAIN, 25));
        matchesDisplayArea.setForeground(Color.white);

        usernameLabel1.setFont(btnFont);
        usernameLabel1.setForeground(Color.white);
        usernameField1.setFont(btnFont);
        usernameLabel2.setFont(btnFont);
        usernameLabel2.setForeground(Color.white);
        usernameField2.setFont(btnFont);
        compareButton.setFont(btnFont);
        backButton.setFont(btnFont);

        usernameLabel1.setBounds(100, 150, 150, 50);
        usernameField1.setBounds(250, 150, 250, 50);
        usernameLabel2.setBounds(100, 220, 150, 50);
        usernameField2.setBounds(250, 220, 250, 50);
        compareButton.setBounds(250, 290, 150, 50);
        comparisonResultArea.setBounds(600, 150, 550, 150);
        matchesDisplayArea.setBounds(100, 360, 1100, 200);
        backButton.setBounds(580, 570, 150, 50);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        compareButton.setBackground(Color.BLACK);
        compareButton.setForeground(Color.WHITE);

        backgroundPanel.add(usernameLabel1);
        backgroundPanel.add(usernameField1);
        backgroundPanel.add(usernameLabel2);
        backgroundPanel.add(usernameField2);
        backgroundPanel.add(compareButton);
        backgroundPanel.add(comparisonResultArea);
        backgroundPanel.add(matchesDisplayArea);
        backgroundPanel.add(backButton);

        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username1 = usernameField1.getText();
                String username2 = usernameField2.getText();
                compareUserData(username1, username2);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    private void compareUserData(String username1, String username2) {
        String playerSql = "SELECT player_id, username FROM players WHERE username = ?";
        String matchSql = "SELECT result, match_date, white_player, black_player FROM matches " +
                "WHERE (white_player = ? AND black_player = ?) OR (white_player = ? AND black_player = ?) " +
                "ORDER BY match_date DESC";

        try (Connection con = getConnection();
             PreparedStatement playerPs1 = con.prepareStatement(playerSql);
             PreparedStatement playerPs2 = con.prepareStatement(playerSql);
             PreparedStatement matchPs = con.prepareStatement(matchSql)) {

            // Query for first user data
            playerPs1.setString(1, username1);
            ResultSet playerResult1 = playerPs1.executeQuery();

            // Query for second user data
            playerPs2.setString(1, username2);
            ResultSet playerResult2 = playerPs2.executeQuery();

            if (playerResult1.next() && playerResult2.next()) {
                int playerId1 = playerResult1.getInt("player_id");
                int playerId2 = playerResult2.getInt("player_id");

                // Query for matches played between the two users
                matchPs.setInt(1, playerId1);
                matchPs.setInt(2, playerId2);
                matchPs.setInt(3, playerId2);
                matchPs.setInt(4, playerId1);
                ResultSet matchResult = matchPs.executeQuery();

                int matchesPlayed = 0;
                int matchesWonByUser1 = 0;
                int matchesWonByUser2 = 0;
                int matchesDrawn = 0;
                StringBuilder matchesData = new StringBuilder("Matches Played Against Each Other (Last 5 Matches):\n");
                StringBuilder allMatchesData = new StringBuilder();

                while (matchResult.next()) {
                    matchesPlayed++;
                    int whitePlayer = matchResult.getInt("white_player");
                    int blackPlayer = matchResult.getInt("black_player");
                    String result = matchResult.getString("result");

                    // Determine the roles of each player
                    String role1 = whitePlayer == playerId1 ? "White" : "Black";
                    String role2 = whitePlayer == playerId2 ? "White" : "Black";

                    if (result.equalsIgnoreCase("White wins")) {
                        if (whitePlayer == playerId1) {
                            matchesWonByUser1++;
                        } else if (whitePlayer == playerId2) {
                            matchesWonByUser2++;
                        }
                    } else if (result.equalsIgnoreCase("Black wins")) {
                        if (blackPlayer == playerId1) {
                            matchesWonByUser1++;
                        } else if (blackPlayer == playerId2) {
                            matchesWonByUser2++;
                        }
                    } else if (result.equalsIgnoreCase("draw")) {
                        matchesDrawn++;
                    }

                    String matchInfo = "Match Date: " + matchResult.getTimestamp("match_date") +
                            " | " + username1 + " as " + role1 +
                            " | " + username2 + " as " + role2 +
                            " | Result: " + result + "\n";

                    allMatchesData.append(matchInfo);
                }

                String[] matchDataLines = allMatchesData.toString().split("\n");
                int totalMatches = matchDataLines.length;
                for (int i = Math.max(0, totalMatches - 5); i < totalMatches; i++) {
                    matchesData.append(matchDataLines[i]).append("\n");
                }

                StringBuilder comparisonResult = new StringBuilder();
                comparisonResult.append("Total Matches Played Against Each Other: ").append(matchesPlayed).append("\n");
                comparisonResult.append(username1).append(" Won ").append(matchesWonByUser1).append(" Matches.\n");
                comparisonResult.append(username2).append(" Won ").append(matchesWonByUser2).append(" Matches.\n");
                comparisonResult.append("Drawn ").append(matchesDrawn).append(" Matches.\n");

                if (matchesPlayed == 0) {
                    matchesData.append("No matches played against each other.");
                }

                comparisonResultArea.setText(comparisonResult.toString());
                matchesDisplayArea.setText(matchesData.toString());

            } else {
                comparisonResultArea.setText("One or both users not found.");
                matchesDisplayArea.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            comparisonResultArea.setText("An error occurred while comparing user data.");
            matchesDisplayArea.setText("");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompareStats compareStatsWindow = new CompareStats();
            compareStatsWindow.setVisible(true);
        });
    }
}
