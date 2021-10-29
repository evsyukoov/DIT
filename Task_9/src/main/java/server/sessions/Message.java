package server.sessions;

public class Message {

    private String msg;

    private boolean send;

    int clientFrom;

    int clientTo;

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getClientFrom() {
        return clientFrom;
    }

    public void setClientFrom(int clientFrom) {
        this.clientFrom = clientFrom;
    }

    public int getClientTo() {
        return clientTo;
    }

    public void setClientTo(int clientTo) {
        this.clientTo = clientTo;
    }
}
