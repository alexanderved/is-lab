package server.network.connections;

import java.util.function.Function;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;

public abstract class Connection {
    public abstract void respond(Function<NetworkRequest, NetworkResponse> responder)
            throws NetworkCommunicationFailedException;
}
