package server.network.connections.states;

import server.network.connections.ClientConnection;

public class ReadingState extends ReadLoopState {
    public ReadingState(ClientConnection connection) {
        super(connection);
    }
}
