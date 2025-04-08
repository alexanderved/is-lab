package common.exceptions;

/**
 * Исключение, выбрасываемое в том случае, если сетевое соединение было разорвано.
 */
public class EndpointDisconnectedException extends RuntimeException {
    public EndpointDisconnectedException() {
        super("Сетевое соединение разорвано");
    }
}
