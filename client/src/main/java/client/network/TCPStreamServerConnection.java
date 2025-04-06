package client.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.utils.CustomDeserializer;
import common.utils.CustomSerializer;

public class TCPStreamServerConnection extends ServerConnection implements Closeable {
    private Socket socket;

    public TCPStreamServerConnection(InetAddress addr, int port) throws IOException {
        socket = new Socket(addr, port);
    }

    @Override
    public NetworkResponse request(NetworkRequest req) throws NetworkCommunicationFailedException {
        try {
            CustomSerializer.serializeTo(socket.getOutputStream(), req);
            
            return (NetworkResponse) CustomDeserializer.deserializeFrom(socket.getInputStream());
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
