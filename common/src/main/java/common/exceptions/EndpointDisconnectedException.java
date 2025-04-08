package common.exceptions;

public class EndpointDisconnectedException extends RuntimeException {
    public EndpointDisconnectedException() {
        super("Сетевое соединение разорвано");
    }
}
