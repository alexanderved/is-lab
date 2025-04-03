package server.network.providers;

import java.util.Set;

import common.exceptions.NetworkCommunicationFailedException;
import server.network.connections.ClientConnection;

public abstract class ConnectionProvider {
    public abstract Set<ClientConnection> provide() throws NetworkCommunicationFailedException;
}
