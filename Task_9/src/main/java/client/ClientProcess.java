package client;

import server.util.Constants;
import server.util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientProcess {

    private final static int PORT = 8080;

    private SocketChannel channel;

    private Selector selector;

    private void printinfo() {
        System.out.println("После того как клиент зарегистрирован на сервере доступны следующие команды:");
        System.out.println("GETALLCLIENTS: Получить id всех подключенных и готовых к диалогу клиентов");
        System.out.println("STARTDIALOG id: Начать диалог c клиентом по id");
        System.out.println("STOPDIALOG session_id: Закончить диалог, клиент становится доступным для новых диалогов");
        System.out.println("GETALLMESSAGES session_id: Получить историю всех сообщений");
        System.out.println("P.S.: Возможность отправлять cообщения доступна после начала диалога (START_DIALOG)");
        System.out.println();
    }

    public void startClient() throws IOException {
        printinfo();
        selector = Selector.open();
        channel = SocketChannel.open(new InetSocketAddress(PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT |
                SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        process();
    }

    /**
     * @param
     * @throws IOException
     */
    public void process() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isConnectable()) {
                        handleConnectable(key);
                    } else if (key.isReadable()) {
                        System.out.println(IOUtils.read(key));
                    } else if (key.isWritable()) {
                        writeToServer(key);
                    }
                    iter.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnectable(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }

    /**
     * Неблокирующее ожидание ввода,
     * каждый виток цикла заходим сюда и смотрим есть ли что читать
     *
     * @param key
     * @throws IOException
     */
    private void writeToServer(SelectionKey key) throws IOException, InterruptedException {
        if (System.in.available() > 0) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(Constants.WRITE_BUFFER_SIZE);
            buffer.clear();
            byte[] tmp = new byte[Constants.WRITE_BUFFER_SIZE];
            int bytes = System.in.read(tmp);
            ByteBuffer buff = ByteBuffer.wrap(tmp, 0, bytes);
            channel.write(buff);
        }
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws IOException {
        new ClientProcess().startClient();
    }
}
