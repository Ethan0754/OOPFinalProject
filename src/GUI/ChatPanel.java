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
        // Allows ChatPanel to call back to the ChatRoom whenever a message is sent
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

        // Wrapping text
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

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
                    JOptionPane.showMessageDialog(ChatPanel.this, "Please enter a username.", "Username Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Send message
                String message = messageField.getText().trim();
                if (!message.isEmpty() && !message.equals(placeholderText)) {
                    eventHandler.onSendMessage(username, message);
                    messageField.setText("");
                    messageField.setForeground(Color.GRAY);
                    messageField.setText(placeholderText);
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

}
