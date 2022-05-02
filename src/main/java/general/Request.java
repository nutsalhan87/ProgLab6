package general;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    private final CommandList command;
    private final List<String> arguments;

    public Request (CommandList cmd, List<String> args) {
        command = cmd;
        arguments = args;
    }

    public CommandList getCommand() {
        return command;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
