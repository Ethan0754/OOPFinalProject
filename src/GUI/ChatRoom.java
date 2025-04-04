package GUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatRoom extends JFrame implements ChatEventHandler{
    // Integer constants
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;

    // Initialize panels
    private ChatPanel chatPanel;

    public ChatRoom() {
        setTitle("Chat Room");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window

        // Create panels
        chatPanel = new ChatPanel(this);

        // Other panels to be added:
        add(chatPanel, BorderLayout.CENTER);
        // ActiveUsersPanel BorderLayout.WEST
        // DirectMessagePanel BorderLayout.EAST

        setVisible(true);

    }

    @Override
    public void onSendMessage(String username, String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(dtf);
        chatPanel.appendMessage(String.format("%s: %s%50s", username, message, time));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatRoom::new);
    }

}
