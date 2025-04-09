package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.ClientConnection;

/**
 * Состояние соединения с клиентом.
 */
public abstract class ConnectionState {
    private final ClientConnection connection;

    /**
     * @param connection Соединение с клиентом.
     */
    protected ConnectionState(ClientConnection connection) {
        this.connection = connection;
    }

    /**
     * Читает запрос от клиента.
     *
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public synchronized void readRequest()
            throws EndpointDisconnectedException {
        
    }

    /**
     * Записывает ответ клиенту.
     *
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public synchronized void writeResponse()
            throws EndpointDisconnectedException {
        
    }

    /**
     * @return запрос от клиента
     */
    public synchronized NetworkRequest pollRequest() {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "не должно находиться в состоянии `" + getClass().getName() + "`");
    }

    /**
     * Устанавливает ответ, который должен быть отправлен клиенту.
     *
     * @param response Ответ клиенту
     */
    public synchronized void offerResponse(NetworkResponse response) {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "не должно находиться в состоянии `" + getClass().getName() + "`");
    }

    /**
     * @return соединение с клиентом
     */
    public synchronized ClientConnection getClientConnection() {
        return connection;
    }
}
