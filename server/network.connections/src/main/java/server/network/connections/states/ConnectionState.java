package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
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
     * @throws NetworkCommunicationFailedException если возникла ошибка сети
     * @throws UnknownDataTypeReceived             если получены данные неизвестного типа
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived, EndpointDisconnectedException {
        
    }

    /**
     * Записывает ответ клиенту.
     *
     * @throws NetworkCommunicationFailedException если возникла ошибка сети
     * @throws EndpointDisconnectedException       если соединение разорвано
     */
    public void writeResponse() throws NetworkCommunicationFailedException, EndpointDisconnectedException {

    }

    /**
     * @return запрос от клиента
     */
    public NetworkRequest pollRequest() {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "должно находиться в другом состоянии");
    }

    /**
     * Устанавливает ответ, который должен быть отправлен клиенту.
     *
     * @param response Ответ клиенту
     */
    public void offerResponse(NetworkResponse response) {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "должно находиться в другом состоянии");
    }

    /**
     * @return соединение с клиентом
     */
    public ClientConnection getClientConnection() {
        return connection;
    }
}
