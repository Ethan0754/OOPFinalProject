import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessages extends Thread {
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
                // Will need to implement for GUI
                System.out.println(message);
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
