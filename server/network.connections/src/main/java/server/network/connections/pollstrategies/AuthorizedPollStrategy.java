package server.network.connections.pollstrategies;

import common.exceptions.EndpointDisconnectedException;
import common.network.requests.NetworkRequest;
import server.network.connections.ClientConnection;

public class AuthorizedPollStrategy extends PollStrategy {
    @Override
    public NetworkRequest poll(ClientConnection connection) throws EndpointDisconnectedException {
        return connection.getState().pollRequest();
    }
}
