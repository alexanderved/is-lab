package server.network.connections;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPChannelServerSideEndpoint extends Endpoint {
    private final SocketChannel channel;

    public TCPChannelServerSideEndpoint(SocketChannel channel) throws IOException {
        this.channel = channel;
        try {
            this.channel.configureBlocking(false);
        } catch (IOException e) {
            this.channel.close();

            throw e;
        }
    }

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public void read(ByteBuffer buffer) throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (!channel.isOpen()) {
            throw new EndpointDisconnectedException();
        }

        try {
            if (channel.read(buffer) < 0) {
                throw new EndpointDisconnectedException();
            }
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public void write(ByteBuffer buffer) throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (!channel.isOpen()) {
            throw new EndpointDisconnectedException();
        }

        try {
            channel.write(buffer);
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (!channel.isOpen()) {
            throw new EndpointDisconnectedException();
        }

        try {
            return channel.socket().getInputStream();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public OutputStream getOutputStream() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (!channel.isOpen()) {
            throw new EndpointDisconnectedException();
        }

        try {
            return channel.socket().getOutputStream();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public SocketAddress getRemoteAddress() throws NetworkCommunicationFailedException {
        try {
            return channel.getRemoteAddress();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
