package server;

import general.Commands;
import general.Port;
import general.Request;
import general.route.Route;
import server.workwithexternaldata.JSONToParsedObject;
import server.workwithexternaldata.ParsedObjectToListRoute;
import server.workwithexternaldata.parsedobjects.ParsedObject;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Route> data = new LinkedList<>();
        try {
            if (!(new File("Data.json").exists())) {
                throw new RuntimeException("Файла данных не существует");
            }
            if ( !(new File("Data.json").canWrite() && new File("Data.json").canRead())) {
                throw new RuntimeException("Ввод или вывод в данный файл не доступен");
            }
            ParsedObject parsedObject = new JSONToParsedObject().parseFile("Data.json");
            data = ParsedObjectToListRoute.convertToListRoute(parsedObject);
        } catch (RuntimeException exc) {
            System.out.println(exc.getMessage());
        }

        /*setupSignalHandler(data);
        setupShutDownWork(data);*/

        ServerSocket serverSocket = Connector.connect(Port.PORT);
        Socket socket = serverSocket.accept();
        while(true) {
            try {
                Request request = GetObject.getObject(socket);
                SendAnswer.sendAnswer(request.getCommand().getExecutableCommand().execute(request.getArguments(), data), socket);
            } catch (SocketException exs) {
                System.out.println("Соединение с клиентом потеряно");
                serverSocket.close();
                serverSocket = Connector.connect(Port.PORT);
                socket = serverSocket.accept();
            }
        }
    }

    /*private void setupSignalHandler(List<Route> data) { //CTRL + Z
        Signal.handle(new Signal("TSTP"), signal -> {
            try {
                Commands.save(new LinkedList<>(), data);
            } catch (IOException excio) {
                System.out.println("Сохранение недоступно. Проблемы с доступом к файлу");
            }
        });
    }

    private void setupShutDownWork(List<Route> data) { //CTRL + C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.exit(0);
        }));
    }*/
}
