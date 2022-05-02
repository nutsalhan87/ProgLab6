package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Connector {
    private Connector() {}

    public static Socket connect(InetAddress inetAddress, int port) throws IOException {
        ServerSocket ssc = new ServerSocket(port);
        return ssc.accept();
    }
}
