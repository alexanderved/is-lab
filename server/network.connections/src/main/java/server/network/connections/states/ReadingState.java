package server.network.connections.states;

import server.network.connections.ClientConnection;

public class WorkingState extends ConnectionState {
    public WorkingState(ClientConnection connection) {
        super(connection);
    }
}
