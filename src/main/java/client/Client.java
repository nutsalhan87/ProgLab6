package client;

import general.Port;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), Port.PORT));
        socketChannel.configureBlocking(false);
        new Interface(socketChannel).startInterface(new Scanner(System.in)::nextLine);
    }
}
