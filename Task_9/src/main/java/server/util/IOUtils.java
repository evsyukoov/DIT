package server.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    public static String read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(Constants.READ_BUFFER_SIZE);
        buffer.clear();
        int bytesRead = channel.read(buffer);
        while (bytesRead > 0) {
            bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                channel.close();
                key.cancel();
            }
            buffer.flip();
        }
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
