package GUI;

// Any class that implements ChatEventHandler must define how to hand a message when sent (Send button is clicked).
public interface ChatEventHandler {
    void onSendMessage(String username, String message);
}
