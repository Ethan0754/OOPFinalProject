import java.io.*;
import java.net.*;

public class Client {

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

            // Takes input from terminal
            in = new DataInputStream(System.in);

            // Sends output to the socket
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Thread to handle receiving messages from the server
            receiveMessagesThread = new Thread(new ReceiveMessages(clientSocket));
            receiveMessagesThread.start();

        } catch (UnknownHostException u) {
            System.out.println("Unknown host: " + u.getMessage());
            return;
        } catch (IOException i) {
            System.out.println("I/O error: " + i.getMessage());
            return;
        }

        // String to read message from input
        String message = "";

        // Keep reading until "Over" is input
        while (!message.equals("Over")) {
            try {
                message = in.readLine();
                sendMessageToServer(message);
            } catch (IOException e) {
                System.out.println("Error reading message: " + e.getMessage());
            }
        }


        receiveMessagesThread.join();

        // Close the connection
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e ) {
            System.out.println("Error closing connection: " + e.getMessage());
        }

    }


    public static void main(String[] args) throws InterruptedException {
        Client c = new Client("3.140.25.145", 5000);
    }


    public void sendMessageToServer(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error sending message to server: " + e.getMessage());
        }
    }


}