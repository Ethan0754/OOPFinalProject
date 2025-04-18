package ClientServerNetwork;
import GUI.ChatEventHandler;
import GUI.ChatPanel;

import java.io.*;
import java.net.*;

public class Client {
    // String and Integer constants
    public static final String SERVER_IP = "3.140.25.145";
    public static final int SERVER_PORT = 5000;
    private static Client instance = null;


    // Initialize socket and input/output streams
    private Socket clientSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Thread receiveMessagesThread;



    // Constructor to put IP address and port
    public Client(String addr, int port) throws InterruptedException {
        // Establish a connection
        try {
            clientSocket = new Socket(addr, port);
            System.out.println("Connected");

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());


        } catch (UnknownHostException u) {
            System.out.println("Unknown host: " + u.getMessage());

        } catch (IOException i) {
            System.out.println("I/O error: " + i.getMessage());

        }
    }


    public static void main(String[] args) throws InterruptedException {
        // ClientServerNetwork.Client c = new ClientServerNetwork.Client("3.140.25.145", 5000);

        // Using String and Integer constants
        Client c = new Client(SERVER_IP, SERVER_PORT);
    }


    public static synchronized Client getInstance() throws InterruptedException {
        if (instance == null) {
            instance = new Client(SERVER_IP, SERVER_PORT);
        }
        return instance;
    }

    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (receiveMessagesThread != null && !receiveMessagesThread.isInterrupted()) {
                receiveMessagesThread.interrupt();
            }
            System.out.println("Connection closed gracefully.");
        } catch (IOException e) {
            System.out.println("Error while closing connection: " + e.getMessage());
        }
    }



    public void sendMessageToServer(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error sending message to server: " + e.getMessage());
        }
    }

    public void startReceiveMessagesThread(ChatEventHandler chatEventHandler) {
        ReceiveMessages receive = new ReceiveMessages(clientSocket, chatEventHandler);
        Thread receiverThread = new Thread(receive);
        receiverThread.start();
    }

}