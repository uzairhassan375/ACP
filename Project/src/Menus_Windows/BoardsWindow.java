package Menus_Windows;

import javax.swing.*;
import java.awt.*;

public class BoardsWindow extends JFrame {
    private static String selection = "Classic";
    private JButton selectedButton;
    private JLabel imageLabel;

    public BoardsWindow(String imagePath) {
        setTitle("Boards");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel(imagePath);
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(5, 0, 10, 0);

        JButton Button1 = createButton("Classic", "Classic");
        JButton Button2 = createButton("Mystic Purple", "MysticPurple");
        JButton Button3 = createButton("Steel Grey", "SteelGrey");
        JButton Button4 = createButton("Wooden", "Wooden");
        JButton Button5 = createButton("Ocean Blue", "OceanBlue");

        backgroundPanel.add(Button1, gbc);
        backgroundPanel.add(Button2, gbc);
        backgroundPanel.add(Button3, gbc);
        backgroundPanel.add(Button4, gbc);
        backgroundPanel.add(Button5, gbc);

        JButton backButton = createBackButton("Back");
        backButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                OptionsWindow o = new OptionsWindow();
                o.setVisible(true);
                dispose();
            });
        });

        gbc.insets = new Insets(15, 0, 10, 0);
        backgroundPanel.add(backButton, gbc);

        imageLabel = new JLabel();
        gbc.insets = new Insets(5, 0, 0, 0);
        backgroundPanel.add(imageLabel, gbc);

        // Load the previously selected image in the background
        SwingUtilities.invokeLater(() -> updateImageLabel(selection));

        // Set the appearance of the previously selected button
        SwingUtilities.invokeLater(() -> selectPreviousButton(backgroundPanel));
    }

    private JButton createButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        styleButton(button);
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> {
            if (selectedButton != null) {
                resetButtonStyle(selectedButton);
            }

            button.setFont(new Font("Monospaced", Font.BOLD, 40));
            button.setBackground(new Color(68, 180, 57, 190));
            button.setOpaque(true);
            selection = button.getActionCommand();
            selectedButton = button;

            SwingUtilities.invokeLater(() -> updateImageLabel(actionCommand));
        });
        return button;
    }

    private JButton createBackButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Monospaced", Font.BOLD, 34));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(222, 22, 213, 0));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
    }

    private void resetButtonStyle(JButton button) {
        button.setFont(new Font("Monospaced", Font.BOLD, 34));
        button.setBackground(new Color(222, 22, 213, 0));
        button.setOpaque(false);
    }

    private void updateImageLabel(String actionCommand) {
        String imagePath = "D:/ACP/Project/assets/" + actionCommand + ".png";
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                return new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            }

            @Override
            protected void done() {
                try {
                    imageLabel.setIcon(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void selectPreviousButton(Container container) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getActionCommand().equals(selection)) {
                    button.setFont(new Font("Monospaced", Font.BOLD, 40));
                    button.setBackground(new Color(68, 180, 57, 190));
                    button.setOpaque(true);
                    selectedButton = button;
                    break;
                }
            }
        }
    }

    public static String getSelection() {
        return selection;
    }

    public static void setSelection(String selection) {
        BoardsWindow.selection = selection;
    }

}

