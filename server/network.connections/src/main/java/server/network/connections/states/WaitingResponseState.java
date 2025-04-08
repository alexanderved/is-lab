package server.network.connections.states;

import common.network.responses.NetworkResponse;
import common.utils.CustomSerializer;
import server.network.connections.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;

public class WaitingResponseState extends ConnectionState {
    protected WaitingResponseState(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void offerResponse(NetworkResponse response) {
        try {
            ByteBuffer bufferedOutput = CustomSerializer.serialize(response);

            getClientConnection().setState(new WriteReadyState(getClientConnection(), bufferedOutput));
        } catch (IOException e) {
            getClientConnection().disconnect();
        }
    }
}
