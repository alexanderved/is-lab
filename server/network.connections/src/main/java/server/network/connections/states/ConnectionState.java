package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.ClientConnection;

public abstract class ConnectionState {
    private final ClientConnection connection;

    protected ConnectionState(ClientConnection connection) {
        this.connection = connection;
    }

    public void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived, EndpointDisconnectedException {
        if (connection.isDisconnected()) {
            throw new EndpointDisconnectedException();
        }
    }

    public void writeResponse() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (connection.isDisconnected()) {
            throw new EndpointDisconnectedException();
        }
    }

    public NetworkRequest pollRequest() {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "должно находиться в другом состоянии");
    }

    public void offerResponse(NetworkResponse response) {
        throw new IllegalStateException("Для выполнения данного метода соединение "
                + "должно находиться в другом состоянии");
    }

    public ClientConnection getClientConnection() {
        return connection;
    }
}
