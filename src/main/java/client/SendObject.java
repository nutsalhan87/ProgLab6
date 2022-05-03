package client;

import general.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SendObject {
    public static <T extends Serializable> void sendObject(T object, SocketChannel socketChannel) throws IOException {
        socketChannel.write(Serializer.serialize(object));
    }
}