package server.network.providers;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import common.exceptions.NetworkCommunicationFailedException;
import server.network.connections.Connection;
import server.network.connections.tcpchannel.TCPChannelConnection;

public class TCPChannelProvider extends Provider {
    private TCPChannelConnection connection = new TCPChannelConnection();
    private ServerSocketChannel serverChannel;

    public TCPChannelProvider(SocketAddress addr) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(addr);
        serverChannel.configureBlocking(false);
    }

    @Override
    public Connection provide() throws NetworkCommunicationFailedException {
        try {
            updateConnection();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }

        return connection;
    }

    private void updateConnection() throws IOException {
        SocketChannel channel = null;
        while ((channel = serverChannel.accept()) != null) {
            connection.addClient(channel);
        }
    }
}
