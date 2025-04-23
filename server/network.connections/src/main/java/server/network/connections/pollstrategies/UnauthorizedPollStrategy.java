package server.network.connections.pollstrategies;

import common.exceptions.EndpointDisconnectedException;
import common.network.requests.LoginRequest;
import common.network.requests.NetworkRequest;
import common.network.requests.RegisterRequest;
import server.exceptions.UnauthorizedRequestException;
import server.logger.Logger;
import server.network.connections.ClientConnection;
import server.network.connections.requests.LoginServerRequest;
import server.network.connections.requests.RegisterServerRequest;

public class UnauthorizedPollStrategy extends PollStrategy {
    @Override
    public NetworkRequest poll(ClientConnection connection) throws UnauthorizedRequestException {
        NetworkRequest req = connection.getState().pollRequest();
        if (req instanceof LoginRequest) {
            return new LoginServerRequest((LoginRequest) req, connection);
        } else if (req instanceof RegisterRequest) {
            return new RegisterServerRequest((RegisterRequest) req, connection);
        } else {
            throw new UnauthorizedRequestException();
        }
    }
}
