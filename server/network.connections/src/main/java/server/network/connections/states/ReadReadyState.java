package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import common.network.requests.NetworkRequest;
import common.utils.CustomDeserializer;
import server.logger.Logger;
import server.network.connections.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ReadReadyState extends ConnectionState {
    private ByteBuffer bufferedInput;

    public ReadReadyState(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived {
        getClientConnection().setState(new WorkingState(getClientConnection()));

        if (bufferedInput == null) {
            bufferedInput = ByteBuffer.allocate(Integer.BYTES);
        }

        if (!readLength()) {
            return;
        }

        if (!readData()) {
            getClientConnection().setState(this);

            return;
        }

        try {
            bufferedInput.rewind();
            NetworkRequest pendingRequest = (NetworkRequest) CustomDeserializer.deserialize(bufferedInput);
            getClientConnection().setState(new HasPendingRequestState(getClientConnection(), pendingRequest));
        } catch (IOException | ClassNotFoundException e) {
            getClientConnection().disconnect();

            throw new UnknownDataTypeReceived(e);
        }

        try {
            Logger.getGlobal().info("Получен запрос от клиента по адресу "
                    + getClientConnection().getEndpoint().getRemoteAddress());
        } catch (NetworkCommunicationFailedException e) {
            Logger.getGlobal().info("Получен запрос от клиента");
        }
    }

    private boolean readLength() throws NetworkCommunicationFailedException {
        if (bufferedInput.capacity() == Integer.BYTES) {
            try {
                getClientConnection().getEndpoint().read(bufferedInput);
            } catch (NetworkCommunicationFailedException e) {
                getClientConnection().disconnect();

                throw new NetworkCommunicationFailedException(e);
            } catch (EndpointDisconnectedException e) {
                getClientConnection().disconnect();

                return false;
            }

            if (bufferedInput.hasRemaining()) {
                getClientConnection().setState(this);

                return false;
            }

            bufferedInput.rewind();
            int length = bufferedInput.getInt();

            bufferedInput = ByteBuffer.allocate(length + Integer.BYTES);
            bufferedInput.putInt(length);
        }

        return true;
    }

    private boolean readData() throws NetworkCommunicationFailedException {
        try {
            getClientConnection().getEndpoint().read(bufferedInput);
        } catch (NetworkCommunicationFailedException e) {
            getClientConnection().disconnect();

            throw new NetworkCommunicationFailedException(e);
        } catch (EndpointDisconnectedException e) {
            getClientConnection().disconnect();

            return false;
        }

        return !bufferedInput.hasRemaining();
    }
}
