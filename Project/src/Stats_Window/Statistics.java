package Stats_Window;

import Menus_Windows.BackgroundPanel;

import javax.swing.*;
import java.awt.*;

    public class Statistics extends JFrame {
        public Statistics() {
            setTitle("Statistics");
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

            JButton userStatsButton = createButton("User Stats");
            JButton compareStatsButton = createButton("Compare Stats");
            JButton backButton = createButton("Back");

            userStatsButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> new Stats().createAndShowGUI());
            });

            compareStatsButton.addActionListener(e -> {
                SwingUtilities.invokeLater(()->new CompareStats().setVisible(true));
            });

            backButton.addActionListener(e -> {
                dispose();
            });

            backgroundPanel.add(userStatsButton, gbc);
            backgroundPanel.add(compareStatsButton, gbc);
            backgroundPanel.add(backButton, gbc);
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

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                Statistics optionsWindow = new Statistics();
                optionsWindow.setVisible(true);
            });
        }
    }