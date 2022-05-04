package server;

import general.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class GetObject {
    public static Request getObject(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return (Request) objectInputStream.readObject();
    }
}
