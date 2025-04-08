package server.network.providers;

import java.util.Set;

import common.exceptions.NetworkCommunicationFailedException;
import server.network.connections.ClientConnection;

/**
 * Провайдер соединений с клиентами.
 */
public abstract class ConnectionProvider {
    /**
     * @return соединения с клиентами, доступные для получения запроса
     * @throws NetworkCommunicationFailedException если возникла ошибка сети
     */
    public abstract Set<ClientConnection> provide() throws NetworkCommunicationFailedException;
}
