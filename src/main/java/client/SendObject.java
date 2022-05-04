package client;

import general.Request;
import general.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SendObject {
    public static <T extends Serializable> void sendObject(Request request, SocketChannel socketChannel) throws IOException {
        socketChannel.write(Serializer.serialize(request));
    }
}