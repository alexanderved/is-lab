package server.network.connections.states;

import common.network.requests.NetworkRequest;
import server.network.connections.ClientConnection;

public class HasPendingRequestState extends ReadLoopState {
    private final NetworkRequest pendingRequest;

    public HasPendingRequestState(ClientConnection connection, NetworkRequest pendingRequest) {
        super(connection);

        this.pendingRequest = pendingRequest;
    }

    @Override
    public synchronized NetworkRequest pollRequest() {
        getClientConnection().setState(new ReadReadyState(getClientConnection()));

        return pendingRequest;
    }
}
