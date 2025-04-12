package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.ClientConnection;

/**
 * Состояние цикла чтения соединения с клиентом.
 */
public abstract class ReadLoopState {
    private final ClientConnection connection;

    /**
     * @param connection Соединение с клиентом.
     */
    protected ReadLoopState(ClientConnection connection) {
        this.connection = connection;
    }

    /**
     * Читает запрос от клиента.
     */
    public synchronized void readRequest() {
        
    }

    /**
     * @return запрос от клиента
     */
    public synchronized NetworkRequest pollRequest() {
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
