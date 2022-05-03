package client;

import client.workwithroute.CreatingNewInstance;
import general.CommandList;
import general.Request;
import general.Serializer;
import general.route.Route;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Interface {
    SocketChannel socketChannel;
    private static int nestingLevel = 0;

    public Interface(SocketChannel sc) {
        socketChannel = sc;
        ++nestingLevel;
    }

    public void startInterface(Input input) {
        if (nestingLevel == 2) {
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
                execCommand(inputLine, input);
                System.out.println(GetAnswer.getAnswer(socketChannel));
            } catch (WrongCommandException exc) {
                System.out.println(exc.getMessage());
            } catch (IOException ioexc) {
                System.out.println("Файл скрипта недоступен.");
            }
        }
    }

    private void execCommand(String command, Input input) throws WrongCommandException, IOException {
        List<String> splittedCommand = new LinkedList<>(Arrays.asList(command.split("\\s+")));
        if (command.equals("") || splittedCommand.size() == 0) {
            throw new WrongCommandException("Введена пустая строка");
        }
        if (splittedCommand.get(0).equals("")) {
            splittedCommand.remove(0);
        }

        switch (CommandList.getCommandList(splittedCommand.get(0))) {
            case HELP:
                SendRequest.sendRequest(new Request(CommandList.HELP, new LinkedList<>()), socketChannel);
                break;
            case INFO:
                SendRequest.sendRequest(new Request(CommandList.INFO, new LinkedList<>()), socketChannel);
                break;
            case SHOW:
                SendRequest.sendRequest(new Request(CommandList.SHOW, new LinkedList<>()), socketChannel);
                break;
            case ADD:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    SendRequest.sendRequest(new Request(CommandList.ADD, new LinkedList<>()), socketChannel);
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    socketChannel.write(Serializer.serialize(route));
                } else
                    throw new WrongCommandException("В коллекцию можно добавить только объект класса Route");
                break;
            case UPDATE:
                if (splittedCommand.size() >= 2) {
                    try {
                        Integer.parseUnsignedInt(splittedCommand.get(1));
                        SendRequest.sendRequest(new Request(CommandList.UPDATE,
                                new LinkedList<>(splittedCommand.subList(1, splittedCommand.size()))), socketChannel);
                        Route route = CreatingNewInstance.createNewRouteInstance(input);
                        socketChannel.write(Serializer.serialize(route));

                    } catch (NumberFormatException exn) {
                        System.out.println("В качестве id должно быть введено целое положительное число");
                    }
                } else
                    throw new WrongCommandException();
                break;
            case REMOVE_BY_ID:
                if (splittedCommand.size() >= 2) {
                    try {
                        Integer.parseUnsignedInt(splittedCommand.get(1));
                        SendRequest.sendRequest(new Request(CommandList.REMOVE_BY_ID,
                                new LinkedList<>(splittedCommand.subList(1, splittedCommand.size()))), socketChannel);
                    } catch (NumberFormatException exn) {
                        System.out.println("В качестве id должно быть введено целое положительное число");
                    }
                } else
                    throw new WrongCommandException();
                break;
            case CLEAR:
                SendRequest.sendRequest(new Request(CommandList.CLEAR, new LinkedList<>()), socketChannel);
                break;
            case EXECUTE_SCRIPT:
                if (splittedCommand.size() >= 2 && new File(splittedCommand.get(1)).exists() && new File(splittedCommand.get(1)).canRead()) {
                    new Interface(socketChannel).startInterface(new BufferedReader(new FileReader(splittedCommand.get(1)))::readLine);
                } else
                    throw new WrongCommandException();
                break;
            case EXIT:
                System.out.println("Осуществлен выход из программы.");
                System.exit(0);
            case ADD_IF_MAX:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    SendRequest.sendRequest(new Request(CommandList.ADD_IF_MAX, new LinkedList<>()), socketChannel);
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    socketChannel.write(Serializer.serialize(route));
                } else
                    System.out.println("Программа поддерживает только работу с Route");
                break;
            case REMOVE_GREATER:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    SendRequest.sendRequest(new Request(CommandList.REMOVE_GREATER, new LinkedList<>()), socketChannel);
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    socketChannel.write(Serializer.serialize(route));
                } else
                    System.out.println("Программа поддерживает только работу с Route");
                break;
            case REMOVE_LOWER:
                if (splittedCommand.size() >= 2 && splittedCommand.get(1).equals("Route")) {
                    SendRequest.sendRequest(new Request(CommandList.REMOVE_LOWER, new LinkedList<>()), socketChannel);
                    Route route = CreatingNewInstance.createNewRouteInstance(input);
                    socketChannel.write(Serializer.serialize(route));
                } else
                    System.out.println("Программа поддерживает только работу с Route");
                break;
            case REMOVE_ANY_BY_DISTANCE:
                if (splittedCommand.size() >= 2) {
                    try {
                        Double.parseDouble(splittedCommand.get(1));
                        SendRequest.sendRequest(new Request(CommandList.REMOVE_ANY_BY_DISTANCE,
                                new LinkedList<>(splittedCommand.subList(1, splittedCommand.size()))), socketChannel);
                    } catch (NumberFormatException exn) {
                        System.out.println("Введите корректную дистанцию в виде вещественного числа");
                    }
                }
                else
                    throw new WrongCommandException();
                break;
            case FILTER_CONTAINS_NAME:
                if (splittedCommand.size() >= 2)
                    SendRequest.sendRequest(new Request(CommandList.HELP,
                            new LinkedList<>(splittedCommand.subList(1, splittedCommand.size()))), socketChannel);
                else
                    throw new WrongCommandException();
                break;
            case FILTER_STARTS_WITH_NAME:
                if (splittedCommand.size() >= 2)
                    SendRequest.sendRequest(new Request(CommandList.FILTER_STARTS_WITH_NAME,
                            new LinkedList<>(splittedCommand.subList(1, splittedCommand.size()))), socketChannel);
                else
                    throw new WrongCommandException();
                break;
            default:
                throw new WrongCommandException();
        }
    }
}

