package server.network.connections.states;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import server.logger.Logger;
import server.network.connections.ClientConnection;

import java.nio.ByteBuffer;

public class WriteReadyState extends ConnectionState {
    private final ByteBuffer bufferedOutput;

    public WriteReadyState(ClientConnection connection, ByteBuffer bufferedOutput) {
        super(connection);

        this.bufferedOutput = bufferedOutput;
    }

    @Override
    public synchronized void writeResponse() throws NetworkCommunicationFailedException {
        getClientConnection().setState(new WorkingState(getClientConnection()));

        getClientConnection().getTaskExecutor().submitWrite(() -> {
            synchronized (this) {
                try {
                    getClientConnection().getEndpoint().write(bufferedOutput);
                } catch (NetworkCommunicationFailedException e) {
                    getClientConnection().disconnect();

                    Logger.getGlobal().warn("Ошибка при записи ответа", e);
                } catch (EndpointDisconnectedException e) {
                    getClientConnection().disconnect();

                    return;
                }

                if (bufferedOutput.hasRemaining()) {
                    getClientConnection().setState(this);

                    return;
                }

                try {
                    Logger.getGlobal().info("Отправлен ответ клиенту по адресу "
                            + getClientConnection().getEndpoint().getRemoteAddress());
                } catch (NetworkCommunicationFailedException e) {
                    Logger.getGlobal().info("Отправлен ответ клиенту");
                }

                getClientConnection().setState(new ReadReadyState(getClientConnection()));
            }
        });
    }
}
