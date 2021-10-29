package server.sessions;

import java.util.ArrayList;
import java.util.List;

public class Session {
    List<Message> messages;

    public Session() {
        messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }
}
