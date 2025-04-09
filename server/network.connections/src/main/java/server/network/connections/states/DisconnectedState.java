package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import server.network.connections.ClientConnection;

public class DisconnectedState extends ConnectionState {
    public DisconnectedState(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void readRequest() throws EndpointDisconnectedException {
        throw new EndpointDisconnectedException();
    }

    @Override
    public void writeResponse() throws EndpointDisconnectedException {
        throw new EndpointDisconnectedException();
    }
}
