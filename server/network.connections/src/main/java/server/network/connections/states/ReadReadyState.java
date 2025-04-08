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
    public synchronized void readRequest()
            throws NetworkCommunicationFailedException, UnknownDataTypeReceived {
        getClientConnection().setState(new WorkingState(getClientConnection()));

        getClientConnection().getTaskExecutor().submitRead(() -> {
            synchronized (this) {
                if (bufferedInput == null) {
                    bufferedInput = ByteBuffer.allocate(Integer.BYTES);
                }

                try {
                    if (!readLength()) {
                        return;
                    }
                } catch (NetworkCommunicationFailedException e) {
                    Logger.getGlobal().warn("Ошибка при чтении запроса", e);
                }

                try {
                    if (!readData()) {
                        getClientConnection().setState(this);

                        return;
                    }
                } catch (NetworkCommunicationFailedException e) {
                    Logger.getGlobal().warn("Ошибка при чтении запроса", e);
                }

                try {
                    bufferedInput.rewind();
                    NetworkRequest pendingRequest =
                            (NetworkRequest) CustomDeserializer.deserialize(bufferedInput);

                    try {
                        Logger.getGlobal().info("Получен запрос от клиента по адресу "
                                + getClientConnection().getEndpoint().getRemoteAddress());
                    } catch (NetworkCommunicationFailedException e) {
                        Logger.getGlobal().info("Получен запрос от клиента");
                    }

                    getClientConnection()
                            .setState(new HasPendingRequestState(getClientConnection(), pendingRequest));
                } catch (IOException | ClassNotFoundException e) {
                    getClientConnection().disconnect();

                    Logger.getGlobal().warn("Ошибка при чтении запроса", e);
                }
            }
        });
    }

    private synchronized boolean readLength() throws NetworkCommunicationFailedException {
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

    private synchronized boolean readData() throws NetworkCommunicationFailedException {
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
