package GUI;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom extends JFrame implements ChatEventHandler, UserUpdateHandler, DirectMessageTriggerHandler {
    // Integer constants
    public static final int CR_WINDOW_WIDTH = 900;
    public static final int CR_WINDOW_HEIGHT = 600;
    public static final int DM_WINDOW_WIDTH = 450;
    public static final int DM_WINDOW_HEIGHT = 300;


    // String constants
    public static final String WINDOW_TITLE = "Chat Room";
    public static final String DATE_FORMAT = "MMMM d, hh:mm a";

    // Initialize panels
    private ChatPanel chatPanel;
    private ActiveUsersPanel activeUsersPanel;
    private Map<String, JFrame> directMessageWindows = new ConcurrentHashMap<>();
    private Map<String, DirectMessagePanel> directMessagePanels = new ConcurrentHashMap<>();

    public ChatRoom() {
        try {
            // Set frame
            setTitle(WINDOW_TITLE);
            setSize(CR_WINDOW_WIDTH, CR_WINDOW_HEIGHT);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Make ChatPanel
            chatPanel = new ChatPanel(this, this);
            setLocationRelativeTo(null); // Centers the window
            setLayout(new BorderLayout());

            // Create Chat and ActiveUser panels
            chatPanel = new ChatPanel(this, this);
            activeUsersPanel = new ActiveUsersPanel();


            // Other panels to be added:
            add(chatPanel, BorderLayout.CENTER);
            add(activeUsersPanel, BorderLayout.WEST);

            activeUsersPanel.setDirectMessageTriggerHandler(this);


            setVisible(true);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    // When a user presses send, it formats the message to include:
    // username, typed message, timestamp, and spacing/wrapping
    @Override
    public void onSendMessage(String username, String message, boolean isDirect) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, hh:mm a");
        String timestamp = LocalDateTime.now().format(formatter);
        String finalOutput;

        if (message.startsWith("USER_LIST_UPDATE=")) {
            String users = message.substring("USER_LIST_UPDATE=".length());
            updateActiveUsers(users.split(","));
            return;
        }

        if (isDirect) {
            String privateMessage = message.substring("DIRECT_MESSAGE=".length());
            String[] partsOfMessage = privateMessage.split(":", 3);

            if (partsOfMessage.length == 3) {
                String senderName = partsOfMessage[0].trim();
                String recipientName = partsOfMessage[1].trim();
                String messageContent = partsOfMessage[2].trim();
                String currentUsername = chatPanel.getUsername();

                String conversationPartner = senderName.equals(currentUsername) ? recipientName : senderName;

                if (!directMessagePanels.containsKey(conversationPartner)) {
                    triggerDirectMessagePanel(conversationPartner);
                }

                DirectMessagePanel targetPanel = directMessagePanels.get(conversationPartner);
                if (targetPanel != null) {
                    targetPanel.appendDirectMessage(senderName + ": " + messageContent + "\n(" + timestamp + ")\n");
                }
                return;
            }
        }

        // prevent duplicate username in output
        if (message.contains(":")) {
            finalOutput = message + "\n(" + timestamp + ")\n";
        }
        else if (chatPanel.getUsername().equals("") || message.startsWith("Welcome")) {
            finalOutput = "Server: " + message + "\n(" + timestamp + ")\n";
        }
        else {
            finalOutput =  message + "\n(" + timestamp + ")\n";
        }

        chatPanel.appendMessage(finalOutput);

    }

    // Updates the active users in the active users panel when a client's username is entered
    @Override
    public void updateActiveUsers(String[] usernames) {
        activeUsersPanel.updateUsers(usernames);
    }

    @Override
    public void triggerDirectMessagePanel(String recipient) {
       if (!directMessagePanels.containsKey(recipient)) {
           DirectMessagePanel newDmPanel = new DirectMessagePanel();
           newDmPanel.setEventHandler(this);
           newDmPanel.setUsername(chatPanel.getUsername());
           newDmPanel.setRecipient(recipient);
           newDmPanel.setBorder(BorderFactory.createTitledBorder("Direct Message with " + recipient));

           JFrame newDmWindow = new JFrame("Direct Message - " + recipient);
           newDmWindow.setSize(DM_WINDOW_WIDTH, DM_WINDOW_HEIGHT);
           newDmWindow.setLocationRelativeTo(this);
           newDmWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
           newDmWindow.add(newDmPanel);

           // Store in maps
           directMessagePanels.put(recipient, newDmPanel);
           directMessageWindows.put(recipient, newDmWindow);
       }

        directMessageWindows.get(recipient).setVisible(true);
    }


    // Run
    public static void main(String[] args){
            SwingUtilities.invokeLater(ChatRoom::new);
    }

}
