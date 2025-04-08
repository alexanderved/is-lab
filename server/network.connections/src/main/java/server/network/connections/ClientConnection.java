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

public class ClientConnection implements Closeable {
    private final Endpoint endpoint;
    private ConnectionState state;

    public ClientConnection(Endpoint endpoint) {
        this.endpoint = endpoint;
        state = new ReadReadyState(this);
    }

    public void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived, EndpointDisconnectedException {
        state.readRequest();
    }

    public void writeResponse() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        state.writeResponse();
    }

    public NetworkRequest pollRequest() {
        return state.pollRequest();
    }

    public void offerResponse(NetworkResponse response) {
        state.offerResponse(response);
    }

    public boolean isDisconnected() {
        return getState() instanceof DisconnectedState;
    }

    public boolean hasPendingRequest() {
        return getState() instanceof HasPendingRequestState;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public void disconnect() {
        setState(new DisconnectedState(this));
    }

    @Override
    public void close() throws IOException {
        endpoint.close();
    }
}
