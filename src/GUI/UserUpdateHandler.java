package GUI;

// Interface used to update the list of users currently connected to the server
public interface UserUpdateHandler {
    void updateActiveUsers(String[] usernames);
}
