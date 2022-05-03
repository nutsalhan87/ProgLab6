package general;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    private final CommandList command;
    private final List<Object> arguments;

    public Request (CommandList cmd, List<Object> args) {
        command = cmd;
        arguments = args;
    }

    public CommandList getCommand() {
        return command;
    }

    public List<Object> getArguments() {
        return arguments;
    }
}
