package server.action;

import server.sessions.Session;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private SocketChannel socket;

    private int id;

    private Session session;

    private boolean isRegistered;

    private List<Request> requests;

    public Client() {
        requests = new ArrayList<>();
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
