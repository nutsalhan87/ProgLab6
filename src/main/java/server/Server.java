package server;

import general.Command;
import general.Port;
import general.Request;
import general.route.Route;
import server.workwithexternaldata.JSONToParsedObject;
import server.workwithexternaldata.ParsedObjectToListRoute;
import server.workwithexternaldata.parsedobjects.ParsedObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = Connector.connect(InetAddress.getLocalHost(), Port.PORT);

        List<Route> data = new LinkedList<>();
        try {
            if (!(new File("./Data.json").exists())) {
                throw new RuntimeException("Файла данных не существует");
            }
            if ( !(new File("./Data.json").canWrite() && new File("./Data.json").canRead())) {
                throw new RuntimeException("Ввод или вывод в данный файл не доступен");
            }
            ParsedObject parsedObject = new JSONToParsedObject().parseFile("./Data.json");
            data = ParsedObjectToListRoute.convertToListRoute(parsedObject);
        } catch (RuntimeException exc) {
            System.out.println(exc.getMessage());
        }

        while(socket.isConnected()) {
            Request request = (Request)(new ObjectInputStream(socket.getInputStream()).readObject());
            request.getCommand().getExecutableCommand().execute(request.getArguments(), data, socket);
        }
    }
}
