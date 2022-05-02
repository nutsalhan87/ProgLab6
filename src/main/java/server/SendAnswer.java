package server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendAnswer {
    private SendAnswer() {}

    public static void sendAnswer(Socket socket, String answer) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        outputStreamWriter.write(answer.toCharArray());
        outputStreamWriter.flush();
    }
}
