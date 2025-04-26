package ClientServerNetwork;

import GUI.ChatEventHandler;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessages extends Thread{
    private Socket clientSocket;
    private DataInputStream inputStream;
    private ChatEventHandler chatEventHandler;

    public ReceiveMessages(Socket clientSocket, ChatEventHandler chatEventHandler) {
        this.clientSocket = clientSocket;
        this.chatEventHandler = chatEventHandler;
    }

    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            String message;

            while (!clientSocket.isClosed()) {
                message = inputStream.readUTF();
                // Will need to implement for GUI
                System.out.println(message);

                if (chatEventHandler != null){
                    String finalMessage = message;
                    SwingUtilities.invokeLater(() -> {
                        boolean isDirect = finalMessage.startsWith("DIRECT_MESSAGE=");
                        chatEventHandler.onSendMessage("",finalMessage, isDirect);

                    });
                }


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
