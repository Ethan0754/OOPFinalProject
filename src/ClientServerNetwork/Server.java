package ClientServerNetwork;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    // Integer constants
    public static final int SERVER_PORT = 5000;


    // Initialize socket and input stream
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    // private List<Socket> clientSockets = new ArrayList<>();
    // Use synchronized list to prevent threading issues while adding or removing a socket
    private List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());


    // Constructor with port
    public Server(int port) {

        // Starts server and waits for a connection
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            while (true) {
                System.out.println("Waiting for a client ...");

                clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);

                System.out.println("Client connected: " + clientSocket);

                new ClientHandler(clientSocket).start();
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static void main(String[] args){
        Server s = new Server(SERVER_PORT);
    }


    public void broadcastMessage(String message){
        for (Socket clientSocket : clientSockets) {
            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                sendMessagesToClients(out, message);
            } catch (IOException e) {
                System.out.println("Error broadcasting message" + e.getMessage());
            }
        }
    }


    public void sendMessagesToClients(DataOutputStream out, String message){
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }


    /**
     * A private nested class that handles communication with a connected client
     * in a multithreaded server application. Each client connection is processed
     * on an individual thread, allowing concurrent handling of multiple clients.
     *
     */
    private class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String username;


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
            try {

                // Create a DataInputStream to read messages from the client
                inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
                // Will need to implement for GUI
                outputStream.writeUTF("Please enter your username: ");
                username = inputStream.readUTF();
                outputStream.writeUTF("Welcome " + username + "!");
                System.out.println("Client (" + clientSocket + ") entered username: " + username);
                String message;


                while ((message = inputStream.readUTF()) != null) {
                    System.out.println("Client (" + clientSocket + ") says: " + message);

                    // Will need to implement for GUI
                    broadcastMessage(username + ": " + message);
                }

                // Will need to implement for GUI
                outputStream.writeUTF(username + " has left the chat room.");
                clientSockets.remove(clientSocket);

                // Will need to implement for GUI
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