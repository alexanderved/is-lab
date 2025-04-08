package server.network.connections.states;

import server.network.connections.ClientConnection;

public class DisconnectedState extends ConnectionState {
    public DisconnectedState(ClientConnection connection) {
        super(connection);
    }
}
