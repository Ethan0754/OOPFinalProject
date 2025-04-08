package GUI;

import javax.swing.*;
import java.awt.*;

public class ActiveUsersPanel extends JPanel {
    // Integer constants
    public static final int PANEL_WIDTH = 150;

    // Make String list to hold the usernames
    private JList<String> userList;

    // Interface
    private DirectMessageTriggerHandler dmHandler;

    public ActiveUsersPanel() {
        // Set panel
        setDirectMessageTriggerHandler(null);
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBorder(BorderFactory.createTitledBorder("Active Users"));
        setLayout(new BorderLayout());

        // Instantiate user list
        userList = new JList<>(new String[] {});
        add(new JScrollPane(userList), BorderLayout.CENTER);

        // Selection listener for DirectMessagePanel
        userList.addListSelectionListener(e -> {
           if (!e.getValueIsAdjusting() && dmHandler != null) {
               String selectedUser = userList.getSelectedValue();
               if (selectedUser != null) {
                   dmHandler.triggerDirectMessagePanel(selectedUser);
               }
           }
        });

    }

    // Updates the list of active users
    public void updateUsers(String[] users) {
        userList.setListData(users);
    }

    public JList<String> getUserList() {
        return userList;
    }

    public void setDirectMessageTriggerHandler(DirectMessageTriggerHandler handler) {
        this.dmHandler = handler;
    }

}
