package general;

import server.SendAnswer;
import server.workwithexternaldata.ListRouteToFileJSON;
import general.route.Route;

import java.io.*;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        String answer = data.stream().map(Route::toString).reduce((s1, s2) -> (s1.concat("\n\n").concat(s2))).orElse("");
        SendAnswer.sendAnswer(socket, answer);
    }

    public static void add(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        data.add((Route)(new ObjectInputStream(socket.getInputStream()).readObject()));
        SendAnswer.sendAnswer(socket,"Новый экземпляр класса успешно добавлен в коллекцию");
    }

    public static void update(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException, ClassNotFoundException {
        Route newRoute = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());
        List<Route> newData = data.stream().map((r) -> {
            if (r.getId().equals(Integer.decode(arguments.get(0)))) {
                return newRoute;
            }
            else
                return r;
        }).collect(Collectors.toList());
        if(data.equals(newData))
            SendAnswer.sendAnswer(socket, "Объекта с таким id нет");
        else
            SendAnswer.sendAnswer(socket, "Объект с id " + arguments.get(0) + " успешно изменен");
        data.clear();
        data.addAll(newData);
    }

    public static void removeById(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException {
        List<Route> newData = data.stream().filter((n) -> (!n.getId().equals(Integer.decode(arguments.get(0))))).collect(Collectors.toList());
        if(data.equals(newData))
            SendAnswer.sendAnswer(socket, "Объекта с таким id нет");
        else
            SendAnswer.sendAnswer(socket, "Объект с id " + arguments.get(0) + " успешно удален");
        data.clear();
        data.addAll(newData);
    }

    public static void clear(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        data = data.stream().filter(n -> false).collect(Collectors.toList());
        SendAnswer.sendAnswer(socket, "Коллекция успешно очищена");
    }

    public static void save(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        new ListRouteToFileJSON().saveInFile(data, new File("Data.json"));
        SendAnswer.sendAnswer(socket, "Коллекция сохранена в файл на сервере");
    }

    public static void exit(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        save(arguments, data, socket);
        System.exit(0);
    }

    public static void addIfMax(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route toAddIfMax = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());

        if (data.stream().max(Route::compareTo).isPresent() && toAddIfMax.compareTo(data.stream().max(Route::compareTo).get()) > 0) {
            data.add(toAddIfMax);
            SendAnswer.sendAnswer(socket,"Новый объект успешно добавлен");
        } else
            SendAnswer.sendAnswer(socket,"Новый объект не больше максимального элемента коллекции, потому не был добавлен");
    }

    public static void removeGreater(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route forComparison = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());
        data = data.stream().filter((n) -> (forComparison.compareTo(n) <= 0)).collect(Collectors.toList());

        SendAnswer.sendAnswer(socket,"Элементы коллекции, превышающие заданный, успешно удалены");
    }

    public static void removeLower(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException {
        Route forComparison = (Route)(new ObjectInputStream(socket.getInputStream()).readObject());
        data = data.stream().filter((n) -> (forComparison.compareTo(n) >= 0)).collect(Collectors.toList());

        SendAnswer.sendAnswer(socket,"Элементы коллекции, которые меньше заданного, успешно удалены");
    }

    public static void removeAnyByDistance(List<String> arguments, List<Route> data, Socket socket) throws NumberFormatException, IOException {
        List<Route> dataWithoutEqualDistances = data.stream()
                .filter((n) -> (!((Double)n.getDistance()).equals(Double.parseDouble(arguments.get(0))))).collect(Collectors.toList());
        List<Route> dataWithEqualDistancesButWithoutFirst = data.stream()
                .filter((n) -> (((Double)n.getDistance()).equals(Double.parseDouble(arguments.get(0))))).skip(1).collect(Collectors.toList());
        data.clear();
        data.addAll(dataWithoutEqualDistances);
        data.addAll(dataWithEqualDistancesButWithoutFirst);
        data = data.stream().sorted(Comparator.comparingInt(Route::getId)).collect(Collectors.toList());
        SendAnswer.sendAnswer(socket, "Первый встречный элемент в коллекции, " +
                "значение distance которого равно заданному, если таковой был найден, удален");
    }

    public static void filterContainsName(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = data.stream().filter((n) -> (n.getName().contains(arguments.get(0)))).map(Route::toString)
                .reduce((n1, n2) -> (n1 + "\n\n" + n2)).orElse("");

        SendAnswer.sendAnswer(socket, answer);
    }

    public static void filterStartsWithName(List<String> arguments, List<Route> data, Socket socket) throws IOException {
        String answer = data.stream().filter((n) -> (n.getName().startsWith(arguments.get(0)))).map(Route::toString)
                .reduce((n1, n2) -> (n1 + "\n\n" + n2)).orElse("");

        SendAnswer.sendAnswer(socket, answer);
    }

    public static void doNothing(List<String> arguments, List<Route> data, Socket socket) {}
}
