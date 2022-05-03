package client;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        while(true) {
            try {
                SocketChannel socketChannel = Connector.connectedSocket();
                new ConsoleInterface(socketChannel).startInterface(new Scanner(System.in)::nextLine);
            } catch (IOException excio) {
                System.out.println(excio.getMessage());
            }
        }
    }
}
