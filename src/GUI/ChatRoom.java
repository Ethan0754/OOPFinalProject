package GUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatRoom extends JFrame implements ChatEventHandler, UserUpdateHandler {
    // Integer constants
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;

    // String constants
    public static final String WINDOW_TITLE = "Test Room";
    public static final String DATE_FORMAT = "MMMM d, hh:mm a";

    // Initialize panels
    private ChatPanel chatPanel;
    private ActiveUsersPanel activeUsersPanel;

    public ChatRoom() {
        // Set frame
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make ChatPanel
        chatPanel = new ChatPanel(this, this);
        setLocationRelativeTo(null); // Centers the window
        setLayout(new BorderLayout());

        // Create panels
        chatPanel = new ChatPanel(this, this);
        activeUsersPanel = new ActiveUsersPanel();

        // Other panels to be added:
        add(chatPanel, BorderLayout.CENTER);
        add(activeUsersPanel, BorderLayout.WEST);
        // DirectMessagePanel BorderLayout.EAST

        setVisible(true);

    }

    // When a user presses send, it formats the message to include:
    // username, typed message, timestamp, and spacing/wrapping
    @Override
    public void onSendMessage(String username, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String timestamp = LocalDateTime.now().format(formatter);
        String fullMessage = username + ": " + message;

        // If a message is longer than 70 characters than append the message normally with the timestamp
        // On a new line, add the timestamp that is right-aligned
        if (fullMessage.length() > 70) {
            chatPanel.appendMessage(fullMessage + String.format("%70s", timestamp));
        } else {
            int paddingLength = Math.max(0, 70 - fullMessage.length());
            String padding = " ".repeat(paddingLength);
            chatPanel.appendMessage(fullMessage + padding + timestamp);
        }

        // TODO: Make it so message text goes for 125 characters in the panel
        //  and making sure no word gets cut off and then have it wrap to the next line.
        //  The date should come after around the first 125 characters separated by two tabs "\t\t".
    }

    // Updates the active users in the active users panel when a client's username is entered
    @Override
    public void updateActiveUsers(String[] usernames) {
        activeUsersPanel.updateUsers(usernames);
    }

    // Run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatRoom::new);
    }

}
