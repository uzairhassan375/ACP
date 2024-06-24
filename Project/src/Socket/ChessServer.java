package Socket;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChessServer {
    public static void main(String[] args) {
        int port = 12345; // Choose a port number

        ExecutorService executor = Executors.newFixedThreadPool(10); // Pool of threads

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Handle each client connection in a new thread
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            // Read client request
            String request = (String) in.readObject();

            if ("Get Help".equals(request)) {
                // Respond with the help servlet URL
                out.writeObject("http://localhost:4000/HelpServlet");
            } else {
                // Handle other requests if needed
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

