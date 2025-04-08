package common.network;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public abstract class Endpoint implements Closeable {
    public abstract void read(ByteBuffer buffer)
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
    public abstract void write(ByteBuffer buffer)
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
    public abstract InputStream getInputStream()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
    public abstract OutputStream getOutputStream()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
    public abstract SocketAddress getRemoteAddress()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
}
