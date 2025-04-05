package server.exceptions;

public class ClientDisconnectedException extends Exception {
    public ClientDisconnectedException() {
        super("Клиент отключен от сервера");
    }
}
