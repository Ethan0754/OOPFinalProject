// Demonstrating Server-side Programming
import java.net.*;
import java.io.*;

public class Server {

    // Initialize socket and input stream
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;

    // Constructor with port
    public Server(int port) {

        // Starts server and waits for a connection
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            while (true) {
                System.out.println("Waiting for a client ...");

                clientSocket = serverSocket.accept();

                System.out.println("Client connected: " + clientSocket);

                new ClientHandler(clientSocket).start();
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        Server s = new Server(5000);
    }


private static class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream inputStream;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
            try {
                // Create a DataInputStream to read messages from the client
                inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                String message;

                // Read messages from the client until "Over" is received
                while ((message = inputStream.readUTF()) != null) {
                    System.out.println("Client (" + clientSocket + ") says: " + message);
                    if (message.equalsIgnoreCase("Over")) {
                        break;
                    }
                }
                System.out.println("Client disconnected: " + clientSocket);

            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {

                try {
                    if (inputStream != null) inputStream.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}