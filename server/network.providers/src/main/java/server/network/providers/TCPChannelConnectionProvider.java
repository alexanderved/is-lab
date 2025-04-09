package server.network.providers;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import server.logger.Logger;
import server.network.connections.ClientConnection;
import server.network.connections.TCPChannelServerSideEndpoint;
import server.network.connections.TaskExecutor;

/**
 * Провайдер соединений с клиентами, использующий TCP-канал.
 */
public class TCPChannelConnectionProvider extends ConnectionProvider implements Closeable {
    private final TaskExecutor executor;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final Set<ClientConnection> connections = new HashSet<>();
    private final Set<ClientConnection> readyConnections = new HashSet<>();

    /**
     * @param addr Адрес сервера
     * @throws IOException если при подключении возникла ошибка сети
     */
    public TCPChannelConnectionProvider(TaskExecutor executor, SocketAddress addr) throws IOException {
        this.executor = executor;
        serverChannel = ServerSocketChannel.open();

        try {
            serverChannel.bind(addr);
            serverChannel.configureBlocking(false);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            serverChannel.close();

            throw e;
        }
    }

    @Override
    public Set<ClientConnection> provide() throws NetworkCommunicationFailedException {
        try {
            selector.select();
        } catch (IOException e) {
            return readyConnections;
        }

        var it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();

            if (key.isAcceptable()) {
                try {
                    acceptClientConnections();
                } catch (IOException e) {
                    if (connections.isEmpty()) {
                        throw new NetworkCommunicationFailedException(e);
                    } else {
                        Logger.getGlobal().error("Ошибка подключения: ", e);
                    }
                }
            } else if (key.isReadable()) {
                ClientConnection c = (ClientConnection) key.attachment();
                readFromClient(c);
            } else if (key.isWritable()) {
                ClientConnection c = (ClientConnection) key.attachment();
                writeToClient(c);
            }

            it.remove();
        }

        for (ClientConnection c : connections) {
            if (c.hasPendingRequest()) {
                readyConnections.add(c);
            }
        }

        return readyConnections;
    }

    private void acceptClientConnections() throws IOException {
        SocketChannel channel = null;
        while ((channel = serverChannel.accept()) != null) {
            addClientConnection(channel);
        }
    }

    private void addClientConnection(SocketChannel channel) {
        try {
            ClientConnection c = new ClientConnection(executor, new TCPChannelServerSideEndpoint(channel));
            connections.add(c);
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, c);

            try {
                Logger.getGlobal().info("Получено подключение от клиента по адресу " + channel.getRemoteAddress());
            } catch (IOException e) {
                Logger.getGlobal().info("Получено подключение от клиента");
            }
        } catch (IOException e) {
            Logger.getGlobal().warn("Не удалось принять подключение от клиента", e);
        }
    }

    private void removeClientConnection(ClientConnection c) {
        connections.remove(c);
        readyConnections.remove(c);

        SocketChannel channel = ((TCPChannelServerSideEndpoint) c.getEndpoint()).getChannel();
        channel.keyFor(selector).cancel();
        try {
            c.close();
        } catch (IOException e) {
            Logger.getGlobal().warn("Не удалось закрыть канал связи с клиентом", e);
        }

        Logger.getGlobal().info("Клиент отключен");
    }

    private void readFromClient(ClientConnection c) {
        try {
            c.readRequest();
        } catch (EndpointDisconnectedException e) {
            removeClientConnection(c);
        }
    }

    private void writeToClient(ClientConnection c) {
        try {
            c.writeResponse();
        } catch (EndpointDisconnectedException e) {
            removeClientConnection(c);
        }
    }

    @Override
    public void close() throws IOException {
        var it = connections.iterator();
        while (it.hasNext()) {
            ClientConnection c = it.next();
            try {
                c.close();
            } catch (IOException e) {
                Logger.getGlobal().warn("Не удалось закрыть соединение с клиентом");
            } finally {
                it.remove();
            }
        }

        serverChannel.close();
    }
}
