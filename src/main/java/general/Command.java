package general;

import general.route.Route;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface Command {
    void execute(List<String> arguments, List<Route> data, Socket socket) throws IOException, ClassNotFoundException;
}
