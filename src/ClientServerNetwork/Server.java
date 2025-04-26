package ClientServerNetwork;

import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Integer constants
    public static final int SERVER_PORT = 5000;


    // Initialize socket and input stream
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    // Use synchronized list to prevent threading issues while adding or removing a socket
    private List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());
    private ConcurrentHashMap<Socket, String> usernameMap = new ConcurrentHashMap<>();

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

    public void broadcastUserList() {
        List<String> usernames = new ArrayList<>(usernameMap.values());
        String userList = String.join(",", usernames);
        String message = "USER_LIST_UPDATE=" + userList;
        broadcastMessage(message);
    }

    public void sendDirectMessage(String senderUsername, String recipientUsername, String message, Socket senderSocket) {
        // send message to recipient
        for (Socket clientSocket : usernameMap.keySet()) {
            if (usernameMap.get(clientSocket).equals(recipientUsername)) {
                try {
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                    sendMessagesToClients(out, "DIRECT_MESSAGE=" + senderUsername + ":" + recipientUsername + ":" + message);
                } catch (IOException e) {
                    System.out.println("Error sending direct message to recipient: " + e.getMessage());
                }
                break;
            }
        }
        // send message to sender (for their dm window)
        try {
            DataOutputStream out = new DataOutputStream(senderSocket.getOutputStream());
            sendMessagesToClients(out, "DIRECT_MESSAGE=" + senderUsername + ":" + recipientUsername + ":" + message);
        } catch (IOException e) {
            System.out.println("Error sending direct message to sender: " + e.getMessage());
        }

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
                outputStream.writeUTF("Please enter your username");
                username = inputStream.readUTF();
                outputStream.writeUTF("Welcome " + username + "!");
                usernameMap.put(clientSocket, username);
                broadcastUserList(); // notify current users connected on connection
                System.out.println("Client (" + clientSocket + ") entered username: " + username);
                String message;


                while ((message = inputStream.readUTF()) != null) {
                    System.out.println("Client (" + clientSocket + ") says: " + message);
                    if (message.startsWith("DIRECT_MESSAGE=")) {
                        String privateMessage = message.substring("DIRECT_MESSAGE=".length());
                        String[] partsOfMessage = privateMessage.split(":", 3);

                        if (partsOfMessage.length == 3) {
                            String senderUsername = partsOfMessage[0];
                            String recipientUsername = partsOfMessage[1];
                            String privateMessageContent = partsOfMessage[2];
                            sendDirectMessage(senderUsername, recipientUsername, privateMessageContent, clientSocket);
                        }
                    }
                    else {
                        broadcastMessage(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                System.out.println("Closing connection: " + clientSocket);
                clientSockets.remove(clientSocket);
                usernameMap.remove(clientSocket);
                broadcastUserList();

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