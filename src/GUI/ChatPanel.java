package GUI;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    private String username;
    private JButton sendButton;

    public ChatPanel() {
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

        // Input panel for message input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField("Enter Text Here:");
        sendButton = new JButton("Send");
        sendButton.setEnabled(false); // Disabling the send button until the username is set.
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    // TODO: Implement action listener to set username when the user submits it

    // TODO: Implement action listener to send messages when the Send button is pressed

    /**
     * Appends a message to the chat area with a newline.
     * @param message The message to display.
     */
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

}
