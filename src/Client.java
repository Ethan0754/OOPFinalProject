// Demonstrating Client-side Programming
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
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // String to read message from input
        String message = "";

        // Keep reading until "Over" is input
        while (!message.equals("Over")) {
            try {
                message = in.readLine();
                sendMessageToServer(message);
            } catch (IOException i) {
                System.out.println(i);
            }
        }


        receiveMessagesThread.join();

        // Close the connection
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException i) {
            System.out.println(i);
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





    private static class ReceiveMessages extends Thread {
        private Socket clientSocket;
        private DataInputStream inputStream;

        public ReceiveMessages(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                String message;

                while (!clientSocket.isClosed()) {
                    message = inputStream.readUTF();
                    System.out.println(clientSocket + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Connection closed or error receiving messages: " + e.getMessage());
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (IOException ex) {
                    System.out.println("Error closing input stream: " + ex.getMessage());
                }
            }
        }
    }
}