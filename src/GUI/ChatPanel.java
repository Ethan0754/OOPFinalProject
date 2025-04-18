package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import ClientServerNetwork.*;

public class ChatPanel extends JPanel implements ChatEventHandler{
    // String constants
    private final String placeholderText = "Enter Text Here";

    // GUI
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    private String username;
    private JButton sendButton;
    private Client client;


    // Interface
    private ChatEventHandler eventHandler;
    private UserUpdateHandler userUpdateHandler;


    public ChatPanel(ChatEventHandler handler, UserUpdateHandler userUpdateHandler) throws InterruptedException {
        // Interface
        this.eventHandler = handler;
        this.userUpdateHandler = userUpdateHandler;

        // The Chat Room Name will eventually be editable
        setBorder(BorderFactory.createTitledBorder("Chat Room Name"));
        setLayout(new BorderLayout());

        // Username input field
        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder("Please enter your username: "));
        // Will eventually change the username field to take up the entire window at the start of the program
        add(usernameField, BorderLayout.NORTH);

        // Area for displaying chat messages
        chatArea = new JTextArea();
        chatArea.setEditable(false);

        // Wrapping text
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Message textbox
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField(placeholderText);
        messageField.setForeground(Color.GRAY);
        setupPlaceholderBehavior();

        // Send button
        sendButton = new JButton("Send");

        // Add panels
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);


        addUsernameListener();
        addSendButtonListener();

        usernameField.setText("Connecting to the server...");
        new Thread(() -> {
            try {

                client = Client.getInstance();
                client.startReceiveMessagesThread(this);

                SwingUtilities.invokeLater(() -> {
                    usernameField.setText("");
                    appendMessage("Connected to the server. Please enter your username:");
                });
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Error: Unable to connect to server.\n" + e.getMessage(),
                        "Connection Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }


    // Action listener for username textbox
    private void addUsernameListener() {
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText().trim();
                if (client != null && !username.isEmpty()) {
                    client.sendMessageToServer(username);
                }
                // If a username is entered, do not allow the user to change it.
                // Can maybe change this so usernames can be changed and updated in ActiveUsers and following messages
                if (!username.isEmpty()) {
                    usernameField.setEditable(false);

                    // Update the active users panel with the username
                    userUpdateHandler.updateActiveUsers(new String[]{username});
                 }
            }
        });
    }

    // Action listener for Send button
    private void addSendButtonListener() {
        // Shared action used by both the button and Enter key
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Does not let a user send a message until they input a username
                if (username == null || username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ChatPanel.this,
                            "Please enter a username.", "Username Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Send message
                String message = messageField.getText().trim();
                if (!message.isEmpty() && !message.equals(placeholderText)) {
                    client.sendMessageToServer(message);
                    eventHandler.onSendMessage(username, message, false);
                    messageField.setText("");

                }
            }
        };

        // Add listeners for Send button and Enter key
        sendButton.addActionListener(sendAction);
        messageField.addActionListener(sendAction);
    }


    // Adds a FocusListener to the message field so that "Enter Text Here:"
    // is cleared when clicked and comes back when clicked off of
    private void setupPlaceholderBehavior() {
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals(placeholderText)) {
                    messageField.setText("");
                    messageField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setForeground(Color.GRAY);
                    messageField.setText(placeholderText);
                }
            }
        });
    }

    // Appends a message to the chat area with a newline.
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    // Gets the client's username
    public String getUsername() {
        return username;
    }

    @Override
    public void onSendMessage(String username, String message, boolean isDirect) {
        SwingUtilities.invokeLater(() -> appendMessage(username + ": " + message));
    }
}
