package GUI;

import javax.swing.*;
import java.awt.*;

public class ChatRoom extends JFrame {
    // Integer constants
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;


    public ChatRoom() {
        setTitle("Chat Room");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window

        ChatPanel chatPanel = new ChatPanel();

        // Other panels to be added:
        add(chatPanel, BorderLayout.CENTER);
        // ActiveUsersPanel BorderLayout.WEST
        // DirectMessagePanel BorderLayout.EAST

        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatRoom::new);
    }

}
