package server;

import server.action.Server;

import java.io.IOException;

public class ServerProcess {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }
}
