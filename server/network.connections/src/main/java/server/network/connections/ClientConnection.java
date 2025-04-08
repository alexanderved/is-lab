package server.network.connections;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import common.network.Endpoint;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.states.ConnectionState;
import server.network.connections.states.DisconnectedState;
import server.network.connections.states.HasPendingRequestState;
import server.network.connections.states.ReadReadyState;

import java.io.Closeable;
import java.io.IOException;

/**
 * Соединение с клиентом.
 */
public class ClientConnection implements Closeable {
    private final Endpoint endpoint;
    private ConnectionState state;

    /**
     * @param endpoint Конечная точка соединения на стороне сервера
     */
    public ClientConnection(Endpoint endpoint) {
        this.endpoint = endpoint;
        state = new ReadReadyState(this);
    }

    /**
     * Читает запрос от клиента.
     *
     * @throws NetworkCommunicationFailedException если возникла ошибка сети
     * @throws UnknownDataTypeReceived             если получены данные неизвестного типа
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public synchronized void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived, EndpointDisconnectedException {
        state.readRequest();
    }

    /**
     * Записывает ответ клиенту.
     *
     * @throws NetworkCommunicationFailedException если возникла ошибка сети
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public synchronized void writeResponse() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        state.writeResponse();
    }

    /**
     * @return запрос от клиента
     */
    public synchronized NetworkRequest pollRequest() {
        return state.pollRequest();
    }

    /**
     * Устанавливает ответ, который должен быть отправлен клиенту.
     *
     * @param response Ответ клиенту
     */
    public synchronized void offerResponse(NetworkResponse response) {
        state.offerResponse(response);
    }

    /**
     * @return {@code true}, если соединение разорвано
     */
    public synchronized boolean isDisconnected() {
        return getState() instanceof DisconnectedState;
    }

    /**
     * @return {@code true}, если присутствует необработанный запрос от клиента
     */
    public synchronized boolean hasPendingRequest() {
        return getState() instanceof HasPendingRequestState;
    }

    /**
     * @return конечную точку на стороне сервера
     */
    public synchronized Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @return текущее состояние соединения.
     */
    public synchronized ConnectionState getState() {
        return state;
    }

    /**
     * Устанавливает состояние соединения.
     *
     * @param state Новое состояние
     */
    public synchronized void setState(ConnectionState state) {
        this.state = state;
    }

    /**
     * Разрывает соединение с клиентом.
     */
    public synchronized void disconnect() {
        setState(new DisconnectedState(this));
    }

    @Override
    public void close() throws IOException {
        endpoint.close();
    }
}
