package client;

import general.Port;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Connector {
    private Connector() {}

    public static SocketChannel connectedSocket() throws IOException {
        for (int i = 1; i <= 5; ++i) {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), Port.PORT));
                socketChannel.configureBlocking(false);
                return socketChannel;
            } catch (IOException exc) {
                System.out.println("Ошибка подключения к серверу. Попытка номер " + i);
            }
        }
        throw new IOException("Подключиться к серверу не вышло");
    }
}
