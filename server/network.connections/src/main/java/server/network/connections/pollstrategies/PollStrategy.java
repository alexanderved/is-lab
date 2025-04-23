package server.network.connections.pollstrategies;

import common.network.requests.NetworkRequest;
import server.exceptions.UnauthorizedRequestException;
import server.network.connections.ClientConnection;

public abstract class PollStrategy {
    public abstract NetworkRequest poll(ClientConnection connection) throws UnauthorizedRequestException;
}
