package client.network;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class TCPSocketClientSideEndpoint extends Endpoint {
    private final Socket socket;

    public TCPSocketClientSideEndpoint(InetAddress addr, int port) throws IOException {
        socket = new Socket(addr, port);
    }

    @Override
    public void read(ByteBuffer buffer) throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (socket.isClosed()) {
            throw new EndpointDisconnectedException();
        }

        byte[] bytes = new byte[buffer.remaining()];
        try {
            int r;
            if ((r = getInputStream().read(bytes)) < 0) {
                throw new EndpointDisconnectedException();
            }

            buffer.put(bytes, 0, r);
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public void write(ByteBuffer buffer) throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (socket.isClosed()) {
            throw new EndpointDisconnectedException();
        }

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        try {
            getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (socket.isClosed()) {
            throw new EndpointDisconnectedException();
        }

        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public OutputStream getOutputStream() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        if (socket.isClosed()) {
            throw new EndpointDisconnectedException();
        }

        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public SocketAddress getRemoteAddress() throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
