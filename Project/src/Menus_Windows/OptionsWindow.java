package Menus_Windows;

import javax.swing.*;
import java.awt.*;

public class OptionsWindow extends JFrame {
    public OptionsWindow() {
        setTitle("Options");
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

        JButton boardsButton = createButton("Boards");
        JButton piecesButton = createButton("Pieces");
        JButton exitButton = createButton("Back");

        boardsButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new BoardsWindow("D:/ACP/Project/assets/Options.png").setVisible(true));
            dispose();
        });

        piecesButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new PiecesWindow("D:/ACP/Project/assets/Options.png").setVisible(true));
            dispose();
        });

        exitButton.addActionListener(e -> {
            dispose();
        });

        backgroundPanel.add(boardsButton, gbc);
        backgroundPanel.add(piecesButton, gbc);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OptionsWindow optionsWindow = new OptionsWindow();
            optionsWindow.setVisible(true);
        });
    }
}
