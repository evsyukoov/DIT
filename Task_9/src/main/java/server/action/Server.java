package server.action;

import server.sessions.ChatRoom;
import server.sessions.Message;
import server.sessions.Session;
import server.util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Server {

    private final List<Client> acceptedClients;

    private static final int PORT = 8080;

    private ServerSocketChannel serverChannel;

    private Selector selector;

    private HashMap<ChatRoom, Session> sessionMap;

    public Server() {
        acceptedClients = new ArrayList<>();
        sessionMap = new HashMap<>();
    }

    public void startServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        nonBlockingLoop();
    }

    public void nonBlockingLoop() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel clientSock = serverChannel.accept();
                        if (clientSock != null) {
                            clientSock.configureBlocking(false);
                            clientSock.register(selector,
                                    SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                        Client client = createNewClient(clientSock);
                        acceptedClients.add(client);
                    } else if (key.isReadable()) {
                        String fromClient = IOUtils.read(key);
                        registerRequest(fromClient, key);
                    } else if (key.isWritable()) {
                        if (isContainsUnrecievedMessages()) {
                            sendToClient();
                        }
                        if (isUnregisteredClients()) {
                            register();
                        }
                        processClientRequests();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void register() throws IOException {
        for (Client client : acceptedClients) {
            if (client.isRegistered()) {
                continue;
            }
            SocketChannel socketChannel = client.getSocket();
            socketChannel.configureBlocking(false);
            String msg = String.format("SERVER: Добро пожаловать, ваш id = %d", client.getId());
            socketChannel.
                    write(ByteBuffer.wrap(msg.getBytes()));
            client.setRegistered(true);
        }
    }

    private void getAllClients(Client cli) throws Exception {
        SocketChannel socketChannel = cli.getSocket();
        socketChannel.configureBlocking(false);
        String msg = acceptedClients.stream()
                .filter(Client::isRegistered)
                .map(Client::getId)
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));

        socketChannel.
                write(ByteBuffer.wrap(msg.getBytes()));
    }

    private boolean isUnregisteredClients() {
        return acceptedClients.stream().
                anyMatch(client -> !client.isRegistered());
    }

    private boolean isContainsUnrecievedMessages() {
        AtomicBoolean result = new AtomicBoolean(false);
        sessionMap.forEach((k, v) -> {
            if (v.getMessages().stream().
                    anyMatch(message -> !message.isSend())) {
                result.set(true);
                return;
            }
        });
        return result.get();
    }

    private List<Message> getMessages() {
        List<Message> result = new ArrayList<>();
        for (Map.Entry<ChatRoom, Session> entry : sessionMap.entrySet()) {
            Session session = entry.getValue();
            result.addAll(session.getMessages().stream()
                    .filter(msg -> !msg.isSend()).collect(Collectors.toList()));
        }
        return result;
    }

    private void registerSession(int idFrom, int idTo) {
        ChatRoom chatRoom = new ChatRoom(idFrom, idTo);
        chatRoom.setFirstChatMemberId(idFrom);
        chatRoom.setSecondChatMemberId(idTo);
        chatRoom.setOpen(false);
        sessionMap.put(chatRoom, new Session());
    }


    private void sendToClient() throws IOException {
        List<Message> messages = getMessages();
        for (Message message : messages) {
            Client client = findClientFromId(message.getClientTo());
            SocketChannel socketChannel = client.getSocket();
            socketChannel.configureBlocking(false);
            String msg = String.format("Клиент N %d: %s", message.getClientFrom(), message.getMsg());
            socketChannel.
                    write(ByteBuffer.wrap(msg.getBytes()));
            message.setSend(true);
        }

    }

    public Client createNewClient(SocketChannel socket) {
        Client client = new Client();
        client.setSocket(socket);
        client.setRegistered(false);
        if (acceptedClients.isEmpty()) {
            client.setId(1);
        } else {
            client.setId(acceptedClients.stream().
                    max((client1, client2) ->
                            Integer.compare((int) client1.getId(), (int) client2.getId()))
                    .get().getId() + 1);
        }
        return client;
    }

    private Client findClientFromId(int id) {
        return acceptedClients.stream()
                .filter(cli -> cli.getId() == id).findFirst()
                .orElse(null);
    }

    public Client getClientFromKey(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        return acceptedClients.stream().
                filter(client -> client.getSocket().equals(channel)).
                findFirst().get();
    }

    private boolean isClientAlreadyInChatRoom(int clientId) {
        return sessionMap.keySet().stream()
                .anyMatch(key -> key.getFirstChatMemberId() == clientId
                        || key.getSecondChatMemberId() == clientId);
    }

    private void registerRequest(String message, SelectionKey key) {
        Client client = getClientFromKey(key);
        if (message.trim().equals("GETALLCLIENTS")) {
            Request request = new Request();
            request.setType(RequestType.GETALLCLIENTS);
            client.getRequests().add(request);
        } else if (message.trim().startsWith("STARTDIALOG")) {
            Integer idTo = parseMessage(message.trim());
            if (idTo == null) {
                return;
            }
            int idFrom = client.getId();
            if (idTo == idFrom || !isValidClientId(idTo)
                    || isClientAlreadyInChatRoom(idTo)
                    || isClientAlreadyInChatRoom(idFrom)) {
                return;
            }
            registerSession(idFrom, idTo);
            Request request = new Request();
            request.setId(idTo);
            request.setType(RequestType.STARTDIALOG);
            client.getRequests().add(request);
        } else if (message.trim().startsWith("STOPDIALOG")) {
            Integer sessionId = parseMessage(message.trim());
            if (sessionId == null) {
                return;
            }
            Request request = new Request();
            request.setType(RequestType.STOPDIALOG);
            request.setId(sessionId);
            client.getRequests().add(request);
        } else if (message.trim().startsWith("GETALLMESSAGES")) {
            Integer sessionId = parseMessage(message.trim());
            if (sessionId == null) {
                return;
            }
            Request request = new Request();
            request.setType(RequestType.GETALLMESSAGES);
            request.setId(sessionId);
            client.getRequests().add(request);
        } else {
            registerMessage(message.trim(), client.getId());
        }
    }

    private Integer parseMessage(String message) {
        String arr[] = message.trim().split(" ");
        if (arr.length != 2 || !arr[1].matches("[0-9]+")) {
            return null;
        }
        return Integer.parseInt(arr[1]);
    }

    private void registerMessage(String text, int idFrom) {
        ChatRoom chatRoom = sessionMap.keySet()
                .stream()
                .filter(key -> key.getFirstChatMemberId() == idFrom ||
                        key.getSecondChatMemberId() == idFrom)
                .findFirst()
                .orElse(null);
        // клиент попытался отправить сообщение не инициализировав ни одного диалога
        if (chatRoom == null) {
            return;
        }
        Message message = new Message();
        message.setMsg(text);
        message.setClientFrom(idFrom);
        message.setClientTo(chatRoom.getFirstChatMemberId() != idFrom
                ? chatRoom.getFirstChatMemberId() : chatRoom.getSecondChatMemberId());
        Session session = sessionMap.get(chatRoom);
        session.getMessages().add(message);
    }

    private void notificateAboutSessionStart() {
        sessionMap.forEach((k, v) -> {
            if (!k.isOpen()) {
                try {
                    sendSimilarMessageForBothClients(k,
                            String.format("Диалог начался, сессия N %d", k.hashCode()));
                    k.setOpen(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isValidClientId(int id) {
        return acceptedClients.stream()
                .anyMatch(client -> client.getId() == id);
    }

    private void messageToClient(Client client, String msg) throws Exception {
        SocketChannel socketChannel = client.getSocket();
        socketChannel.configureBlocking(false);
        socketChannel.
                write(ByteBuffer.wrap(msg.getBytes()));
    }

    private void processClientRequests() {
        acceptedClients.stream()
                .filter(client -> !client.getRequests().isEmpty())
                .forEach(client -> {
                    Iterator<Request> iter = client.getRequests().iterator();
                    while (iter.hasNext()) {
                        // обрабатываем запрос клиента и удаляем из очереди
                        Request req = iter.next();
                        try {
                            if (req.getType() == RequestType.GETALLCLIENTS) {
                                getAllClients(client);
                            } else if (req.getType() == RequestType.STARTDIALOG) {
                                notificateAboutSessionStart();
                            } else if (req.getType() == RequestType.STOPDIALOG) {
                                stopDialogProcess(req.getId());
                            } else if (req.getType() == RequestType.GETALLMESSAGES) {
                                getAllMessagesProcess(client, req.getId());
                            }
                            iter.remove();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getAllMessagesProcess(Client client, int sessionId) throws Exception {
        ChatRoom chatRoom;
        if ((chatRoom = getChatRoomFromSessionId(sessionId)) != null) {
            Session session = sessionMap.get(chatRoom);
            StringBuilder sb = new StringBuilder();
            session.getMessages()
                    .forEach(msg -> sb.append(String.format("Клиент N %d: ", msg.getClientFrom()))
                            .append(msg.getMsg())
                            .append("\n"));
            messageToClient(client, sb.toString());
        }
    }

    private ChatRoom getChatRoomFromSessionId(int sessionId) {
        return sessionMap.keySet()
                .stream()
                .filter(chatRoom -> chatRoom.hashCode() == sessionId)
                .findFirst()
                .orElse(null);
    }

    private void stopDialogProcess(int sessionId) throws Exception {
        ChatRoom chatRoom;
        if ((chatRoom = getChatRoomFromSessionId(sessionId)) != null) {
            sendSimilarMessageForBothClients(chatRoom,
                    String.format("Диалог N %d закончен!", sessionId));
            sessionMap.remove(chatRoom);
        }
    }

    private void sendSimilarMessageForBothClients(ChatRoom chatRoom, String text) throws Exception {
        Client first = findClientFromId(chatRoom.getFirstChatMemberId());
        Client second = findClientFromId(chatRoom.getSecondChatMemberId());
        messageToClient(first, text);
        messageToClient(second, text);
    }
}
