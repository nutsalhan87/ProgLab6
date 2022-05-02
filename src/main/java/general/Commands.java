package general;

import server.SendAnswer;
import server.workwithexternaldata.ListRouteToFileJSON;
import general.route.Route;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class Commands {
    private Commands() {}

    public static void help(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer ="help: вывести справку по доступным командам\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add {element}: добавить новый элемент в коллекцию\n" +
                "update id: обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id id: удалить элемент из коллекции по его id\n" +
                "clear: очистить коллекцию\n" +
                "save: сохранить коллекцию в файл\n" +
                "execute_script file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit: завершить программу (без сохранения в файл)\n" +
                "add_if_max {element}: добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "remove_greater {element}: удалить из коллекции все элементы, превышающие заданный\n" +
                "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "remove_any_by_distance distance: удалить из коллекции один элемент, значение поля distance которого эквивалентно заданному\n" +
                "filter_contains_name name: вывести элементы, значение поля name которых содержит заданную подстроку\n" +
                "filter_starts_with_name name: вывести элементы, значение поля name которых начинается с заданной подстроки";
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void info(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = "Тип коллекции: " + data.getClass().getName() + "\n" +
                "Тип данных, хранимых в коллекции: " + Route.class.getName() + "\n" +
                "Количество элементов в коллекции: " + data.size();
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void show(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = "";
        for (int i = 0; i < data.size(); i++) {
            answer += data.get(i) + "\n\n";
        }
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void add(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        data.add((Route)(new ObjectInputStream(socket.getInputStream()).readObject()));
        SendAnswer.sendAnswer(socket,"Новый экземпляр класса успешно добавлен в коллекцию");
    }

    public static void update(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException, ClassNotFoundException {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(Integer.decode(arguments.get(0)))) {
                data.get(i).updateValues((Route)(new ObjectInputStream(socket.getInputStream()).readObject()));
                SendAnswer.sendAnswer(socket, "Объект с id " + arguments.get(0) + " успешно изменен");
                return;
            }
        }
        SendAnswer.sendAnswer(socket, "Объекта с таким id нет");
    }

    public static void removeById(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(Integer.decode(arguments.get(0)))) {
                data.remove(i);
                SendAnswer.sendAnswer(socket, "Объект с id " + arguments.get(0) + " успешно удален");
                return;
            }
        }
        SendAnswer.sendAnswer(socket, "Объекта с таким id нет");
    }

    public static void clear(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        data.clear();
        SendAnswer.sendAnswer(socket, "Коллекция успешно очищена");
    }

    public static void save(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        new ListRouteToFileJSON().saveInFile(data, new File("./Data.json"));
        SendAnswer.sendAnswer(socket, "Коллекция сохранена в файл на сервере");
    }

    public static void addIfMax(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route toAddIfMax = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());

        if (toAddIfMax.compareTo(Collections.max(data)) > 0) {
            data.add(toAddIfMax);
            SendAnswer.sendAnswer(socket,"Новый объект успешно добавлен");
        } else
            SendAnswer.sendAnswer(socket,"Новый объект не больше максимального элемента коллекции, потому не был добавлен");
    }

    public static void removeGreater(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route forComparison = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());

        for (int i = data.size() - 1; i >= 0; i--) {
            if (forComparison.compareTo(data.get(i)) < 0)
                data.remove(i);
        }

        SendAnswer.sendAnswer(socket,"Элементы коллекции, превышающие заданный, успешно удалены");
    }

    public static void removeLower(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route forComparison = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());

        for (int i = data.size() - 1; i >= 0; i--) {
            if (forComparison.compareTo(data.get(i)) > 0)
                data.remove(i);
        }

        SendAnswer.sendAnswer(socket,"Элементы коллекции, которые меньше заданного, успешно удалены");
    }

    public static void removeAnyByDistance(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException {
        boolean isFound = false;
        for (int i = 0; i < data.size(); i++) {
            if (Math.abs(data.get(i).getDistance() - Double.parseDouble(arguments.get(0))) < 0.00000001d) {
                data.remove(i);
                isFound = true;
                break;
            }
        }
        if (isFound)
            SendAnswer.sendAnswer(socket, "Первый встречный элемент в коллекции, значение distance которого равно заданному, удален");
        else
            SendAnswer.sendAnswer(socket,"Элемент, значение distance которого равно заданному, не найден");
    }

    public static void filterContainsName(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().contains(arguments.get(0)))
                answer += data.get(i) + "\n\n";
        }
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void filterStartsWithName(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().startsWith(arguments.get(0)))
                answer += data.get(i) + "\n\n";
        }
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void doNothing(List<String> arguments, List<Route> data, Socket socket) {}
}
