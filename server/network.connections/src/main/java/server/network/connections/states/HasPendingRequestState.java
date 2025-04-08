package server.network.connections.states;

import common.network.requests.NetworkRequest;
import server.network.connections.ClientConnection;

public class HasPendingRequestState extends ConnectionState {
    private final NetworkRequest pendingRequest;

    public HasPendingRequestState(ClientConnection connection, NetworkRequest pendingRequest) {
        super(connection);

        this.pendingRequest = pendingRequest;
    }

    @Override
    public NetworkRequest pollRequest() {
        getClientConnection().setState(new WaitingResponseState(getClientConnection()));

        return pendingRequest;
    }
}
