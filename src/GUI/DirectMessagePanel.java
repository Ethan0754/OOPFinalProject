package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DirectMessagePanel extends JPanel {
    // String constants
    public static final String placeholderText = "Enter Text Here:";

    // GUI
    private JTextArea dmArea;
    private JTextField dmField;
    private JButton dmSendButton;
    private String username;

    // Interface
    private ChatEventHandler eventHandler;

    public DirectMessagePanel() {
        // Set panel
        setPreferredSize(new Dimension(200, 0));
        setLayout(new BorderLayout());

        // Set dmArea
        dmArea = new JTextArea();
        dmArea.setEditable(false);
        dmArea.setLineWrap(true);
        dmArea.setWrapStyleWord(true);
        JScrollPane dmScroll = new JScrollPane(dmArea);
        add(dmScroll, BorderLayout.CENTER);

        // Set dmInputPanel
        JPanel dmInputPanel = new JPanel(new BorderLayout());
        dmField = new JTextField(placeholderText);
        dmField.setForeground(Color.GRAY);
        setupPlaceholderBehavior();

        // Send button
        dmSendButton = new JButton("Send");

        // Add panels
        dmInputPanel.add(dmField, BorderLayout.CENTER);
        dmInputPanel.add(dmSendButton, BorderLayout.EAST);
        add(dmInputPanel, BorderLayout.SOUTH);

        // Add send button listener
        addSendButtonListener();

    }
    // Action listener for Send button
    private void addSendButtonListener() {
        // Shared action used by both the button and Enter key
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Does not let a user send a message until they input a username in the ChatPanel
                if (username == null || username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(DirectMessagePanel.this,
                            "Username not set in ChatPanel.", "Username Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Send message
                String message = dmField.getText().trim();
                if (!message.isEmpty() && !message.equals(placeholderText)) {
                    eventHandler.onSendMessage(username, message, true);
                    dmField.setText("");
                }
            }
        };

        // Add listeners for Send button and Enter key
        dmSendButton.addActionListener(sendAction);
        dmField.addActionListener(sendAction);
    }


    // Focus listener for dmField
    // Adds a FocusListener to the message field so that "Enter Text Here:"
    // is cleared when clicked and comes back when clicked off of
    private void setupPlaceholderBehavior() {
        dmField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dmField.getText().equals(placeholderText)) {
                    dmField.setText("");
                    dmField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dmField.getText().isEmpty()) {
                    dmField.setForeground(Color.GRAY);
                    dmField.setText(placeholderText);
                }
            }
        });
    }
    // Appends a message to the dm chat area with a newline.
    public void appendDirectMessage(String message) {
        dmArea.append(message + "\n");
    }

    // Sets the username of the client initiating the DM
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEventHandler(ChatEventHandler handler) {
        this.eventHandler = handler;
    }

}
