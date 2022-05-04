package client;

import java.net.ConnectException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        System.out.println("Введите порт");
        int port = new Scanner(System.in).nextInt();

        while (true) {
            try {
                SocketChannel socketChannel = Connector.connectedSocket(port);
                new ConsoleInterface(socketChannel).startInterface(new Scanner(System.in)::nextLine);
            } catch (ConnectException conex) {
                System.out.println(conex.getMessage());
                System.out.println("Введите порт");
                port = new Scanner(System.in).nextInt();
            }
        }
    }
}
