package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ChatPanel extends JPanel {
    // String constants
    private final String placeholderText = "Enter Text Here";

    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    private String username;
    private JButton sendButton;
    private ChatEventHandler eventHandler;

    public ChatPanel(ChatEventHandler handler) {
        this.eventHandler = handler;

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
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Message textbox
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField(placeholderText);
        messageField.setForeground(Color.GRAY);
        setupPlaceholderBehavior();

        // Send button
        sendButton = new JButton("Send");
        sendButton.setEnabled(false); // Disabling the send button until the username is set.

        // Add panels
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners for Username and Send
        addUsernameListener();
        addSendButtonListener();
    }

    // Action listener for Username textbox
    private void addUsernameListener() {
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    usernameField.setEditable(false);
                    sendButton.setEnabled(true);
                }
            }
        });
    }

    // Action listener for Send button
    private void addSendButtonListener() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (username != null && !username.isEmpty()) {
                    String message = messageField.getText().trim();
                    if (!message.isEmpty()) {
                        eventHandler.onSendMessage(username, message);
                        messageField.setText("");
                    }
                }
            }
        });
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

    /**
     * Appends a message to the chat area with a newline.
     * @param message The message to display.
     */
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

}
