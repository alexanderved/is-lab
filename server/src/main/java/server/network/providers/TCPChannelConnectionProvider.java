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
import java.util.logging.Level;

import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import server.exceptions.ClientDisconnectedException;
import server.logger.Logger;
import server.network.connections.ClientConnection;
import server.network.connections.TCPChannelClientConnection;

public class TCPChannelConnectionProvider extends ConnectionProvider implements Closeable {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Set<TCPChannelClientConnection> connections = new HashSet<>(); 
    private Set<ClientConnection> readyConnections = new HashSet<>();

    public TCPChannelConnectionProvider(SocketAddress addr) throws IOException {
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
                TCPChannelClientConnection c = (TCPChannelClientConnection) key.attachment();
                readFromClient(c);
            } else if (key.isWritable()) {
                TCPChannelClientConnection c = (TCPChannelClientConnection) key.attachment();
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
            TCPChannelClientConnection c = new TCPChannelClientConnection(channel);
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

    private void removeClientConnection(TCPChannelClientConnection c) {
        connections.remove(c);
        readyConnections.remove(c);
        c.getChannel().keyFor(selector).cancel();
        try {
            c.close();
        } catch (IOException e) {
            Logger.getGlobal().warn("Не удалось закрыть канал связи с клиентом", e);
        }

        Logger.getGlobal().info("Клиент отключен");
    }

    private void readFromClient(TCPChannelClientConnection c) {
        try {
            c.readRequest();
        } catch (NetworkCommunicationFailedException | UnknownDataTypeReceived e) {
            Logger.getGlobal().warn("Ошибка коммуникации с клиентом", e);

            removeClientConnection(c);
        } catch (ClientDisconnectedException e) {
            removeClientConnection(c);
        }
    }

    private void writeToClient(TCPChannelClientConnection c) {
        try {
            c.writeResponse();
        } catch (NetworkCommunicationFailedException e) {
            Logger.getGlobal().warn("Ошибка коммуникации с клиентом", e);
            
            removeClientConnection(c);
        } catch (ClientDisconnectedException e) {
            removeClientConnection(c);
        }
    }

    @Override
    public void close() throws IOException {
        var it = connections.iterator();
        while (it.hasNext()) {
            TCPChannelClientConnection c = it.next();
            try {
                c.close();
            } catch (IOException e) {

            } finally {
                it.remove();
            }
        }

        serverChannel.close();
    }
}
