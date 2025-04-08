package client.network;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.Endpoint;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.utils.CustomDeserializer;
import common.utils.CustomSerializer;

import java.io.Closeable;
import java.io.IOException;

public class ServerConnection implements Closeable {
    private final Endpoint endpoint;

    public ServerConnection(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public NetworkResponse request(NetworkRequest req)
            throws NetworkCommunicationFailedException, EndpointDisconnectedException {
        try {
            CustomSerializer.serializeTo(endpoint.getOutputStream(), req);

            return (NetworkResponse) CustomDeserializer.deserializeFrom(endpoint.getInputStream());
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    @Override
    public void close() throws IOException {
        endpoint.close();
    }
}
