package server.network.providers;

import common.exceptions.NetworkCommunicationFailedException;
import server.network.connections.Connection;

public abstract class ConnectionProvider {
    public abstract Connection provide() throws NetworkCommunicationFailedException;
}
