package server.action;

public class Request {

    RequestType type;

    //id клиента или сессии, или null
    Integer id;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
