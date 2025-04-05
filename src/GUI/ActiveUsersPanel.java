package GUI;

import javax.swing.*;
import java.awt.*;

public class ActiveUsersPanel extends JPanel {
    // Integer constants
    public static final int PANEL_WIDTH = 150;

    // Make String list to hold the usernames
    private JList<String> userList;

    public ActiveUsersPanel() {
        // Set panel
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBorder(BorderFactory.createTitledBorder("Active Users"));
        setLayout(new BorderLayout());

        // Instantiate user list
        userList = new JList<>(new String[] {});
        add(new JScrollPane(userList), BorderLayout.CENTER);

    }

    // Updates the list of active users
    public void updateUsers(String[] users) {
        userList.setListData(users);
    }

    public JList<String> getUserList() {
        return userList;
    }

}
