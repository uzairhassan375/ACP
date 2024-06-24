package Socket;

import Menus_Windows.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChessClient {

    public static void main(String[] args) {
        String serverAddress = "192.168.1.2"; // Replace with your server address
        int port = 12345; // Replace with your server port

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to server on port " + port);

            // Start a thread to listen for messages from the server
            new Thread(new ServerListener(socket)).start();

            // Show the MainMenu GUI
            SwingUtilities.invokeLater(() -> {
                MainMenu mainMenu = new MainMenu(); // Pass socket to MainMenu if needed
                mainMenu.setVisible(true);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Separate thread to listen for server messages
    public static void startHelp(Component parent) {
        try (Socket socket = new Socket("192.168.1.2", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send request to server
            out.writeObject("Get Help");

            // Receive response from server
            String helpURL = (String) in.readObject();

            // Open the URL in the default browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(helpURL));
            } else {
                JOptionPane.showMessageDialog(parent, "Desktop browsing not supported.");
            }
        } catch (IOException | ClassNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "An error occurred: " + e.getMessage());
        }
    }

    static class ServerListener implements Runnable {
        private Socket socket;

        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Server says: " + message);

                    // Display message in GUI (update the GUI in EDT)
                    SwingUtilities.invokeLater(() -> {
                        MainMenu mainMenu = new MainMenu(); // Pass socket to MainMenu if needed
                        mainMenu.setVisible(true);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

