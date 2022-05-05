package client;

import client.workwithroute.CreatingNewInstance;
import general.CommandList;
import general.Request;
import general.route.Route;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ConsoleInterface {
    private static int nestingLevel = 0;
    private SocketChannel socketChannel;

    public ConsoleInterface(SocketChannel sc) {
        ++nestingLevel;
        socketChannel = sc;
    }

    public void startInterface(Input input) {
        if (nestingLevel > 2) {
            System.out.println("Нельзя вызвать один скрипт внутри другого или того же скрипта");
            --nestingLevel;
            return;
        }
        String inputLine;
        while (true) {
            try {
                inputLine = input.readLine();
                if (inputLine == null) {
                    --nestingLevel;
                    return;
                }
            } catch (IOException exio) {
                System.out.println("Такого файла нет");
                --nestingLevel;
                return;
            }

            try {
                Request request = execCommand(inputLine, input);
                if (CommandList.NO_COMMAND.equals(request.getCommand())) {
                    continue;
                }
                try {
                    SendRequest.sendRequest(request, socketChannel);
                    System.out.println(GetAnswer.getAnswer(socketChannel));
                } catch (IOException ioexc) {
                    System.out.println(ioexc.getMessage());
                    socketChannel.close();
                    --nestingLevel;
                    return;
                }
            } catch (WrongCommandException exc) {
                System.out.println(exc.getMessage());
            } catch (IOException ioexc) {
                System.out.println("Файл скрипта недоступен.");
            }
        }
    }

    private Request execCommand(String command, Input input) throws WrongCommandException, IOException {
        List<String> splittedCommand = new LinkedList<>(Arrays.asList(command.split("\\s+")));
        if (command.equals("") || splittedCommand.size() == 0) {
            throw new WrongCommandException("Введена пустая строка");
        }
        if (splittedCommand.get(0).equals("")) {
            splittedCommand.remove(0);
        }

        switch (CommandList.getCommandList(splittedCommand.get(0))) {
            case HELP:
                return new Request(CommandList.HELP, new LinkedList<>());
            case INFO:
                return new Request(CommandList.INFO, new LinkedList<>());
            case SHOW:
                return new Request(CommandList.SHOW, new LinkedList<>());
            case ADD:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    return new Request(CommandList.ADD, Arrays.asList(route));
                } else
                    throw new WrongCommandException("В коллекцию можно добавить только объект класса Route");
            case UPDATE:
                if (splittedCommand.size() >= 2) {
                    try {
                        Integer.parseUnsignedInt(splittedCommand.get(1));
                        List<Object> objList = new LinkedList<>();
                        objList.add(CreatingNewInstance.createNewRouteInstance(input));
                        objList.addAll(splittedCommand.subList(1, splittedCommand.size()));
                        return new Request(CommandList.UPDATE, objList);
                    } catch (NumberFormatException exn) {
                        throw new WrongCommandException("В качестве id должно быть введено целое положительное число");
                    }
                } else
                    throw new WrongCommandException();
            case REMOVE_BY_ID:
                if (splittedCommand.size() >= 2) {
                    try {
                        Integer.parseUnsignedInt(splittedCommand.get(1));
                        return new Request(CommandList.REMOVE_BY_ID,
                                new LinkedList<>(splittedCommand.subList(1, splittedCommand.size())));
                    } catch (NumberFormatException exn) {
                        throw new WrongCommandException("В качестве id должно быть введено целое положительное число");
                    }
                } else
                    throw new WrongCommandException();
            case CLEAR:
                return new Request(CommandList.CLEAR, new LinkedList<>());
            case EXECUTE_SCRIPT:
                if (splittedCommand.size() >= 2 && new File(splittedCommand.get(1)).exists() && new File(splittedCommand.get(1)).canRead()) {
                    new ConsoleInterface(socketChannel).startInterface(new BufferedReader(new FileReader(splittedCommand.get(1)))::readLine);
                    return new Request(CommandList.NO_COMMAND, new LinkedList<>());
                } else
                    throw new WrongCommandException();
            case EXIT:
                System.out.println("Осуществлен выход из программы.");
                System.exit(0);
            case ADD_IF_MAX:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    return new Request(CommandList.ADD_IF_MAX, Arrays.asList(route));
                } else
                    throw new WrongCommandException("Программа поддерживает только работу с Route");
            case REMOVE_GREATER:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    return new Request(CommandList.REMOVE_GREATER, Arrays.asList(route));
                } else
                    throw new WrongCommandException("Программа поддерживает только работу с Route");
            case REMOVE_LOWER:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    return new Request(CommandList.REMOVE_LOWER, Arrays.asList(route));
                } else
                    throw new WrongCommandException("Программа поддерживает только работу с Route");
            case REMOVE_ANY_BY_DISTANCE:
                if (splittedCommand.size() >= 2) {
                    try {
                        Double.parseDouble(splittedCommand.get(1));
                        return new Request(CommandList.REMOVE_ANY_BY_DISTANCE,
                                new LinkedList<>(splittedCommand.subList(1, splittedCommand.size())));
                    } catch (NumberFormatException exn) {
                        throw new WrongCommandException("Введите корректную дистанцию в виде вещественного числа");
                    }
                } else
                    throw new WrongCommandException();
            case FILTER_CONTAINS_NAME:
                if (splittedCommand.size() >= 2)
                    return new Request(CommandList.HELP,
                            new LinkedList<>(splittedCommand.subList(1, splittedCommand.size())));
                else
                    throw new WrongCommandException();
            case FILTER_STARTS_WITH_NAME:
                if (splittedCommand.size() >= 2)
                    return new Request(CommandList.FILTER_STARTS_WITH_NAME,
                            new LinkedList<>(splittedCommand.subList(1, splittedCommand.size())));
                else
                    throw new WrongCommandException();
            default:
                throw new WrongCommandException();
        }
    }
}

