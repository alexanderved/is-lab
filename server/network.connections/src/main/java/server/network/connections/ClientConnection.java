package server.network.connections;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.Endpoint;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.utils.CustomSerializer;
import server.exceptions.UnauthorizedRequestException;
import server.logger.Logger;
import server.network.connections.pollstrategies.PollStrategy;
import server.network.connections.pollstrategies.UnauthorizedPollStrategy;
import server.network.connections.states.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Соединение с клиентом.
 */
public class ClientConnection implements Closeable {
    private final TaskExecutor executor;
    private final Endpoint endpoint;
    private PollStrategy pollStrategy;
    private ReadLoopState state;
    private boolean isDisconnected = false;

    private final Queue<ByteBuffer> bufferedOutput = new LinkedList<>();
    private boolean isWriting = false;

    /**
     * @param endpoint Конечная точка соединения на стороне сервера
     */
    public ClientConnection(TaskExecutor executor, Endpoint endpoint) {
        this.executor = executor;
        this.endpoint = endpoint;
        pollStrategy = new UnauthorizedPollStrategy();
        state = new ReadReadyState(this);
    }

    /**
     * Читает запрос от клиента.
     *
     * @throws EndpointDisconnectedException если соединение разорвано
     */
    public synchronized void readRequest() throws EndpointDisconnectedException {
        if (isDisconnected()) {
            throw new EndpointDisconnectedException();
        }

        state.readRequest();
    }

    /**
     * Записывает ответ клиенту.
     *
     * @throws EndpointDisconnectedException если соединение разорвано
     */
    public synchronized void writeResponse() throws EndpointDisconnectedException {
        if (isDisconnected()) {
            throw new EndpointDisconnectedException();
        }

        if (isWriting || bufferedOutput.isEmpty()) {
            return;
        }

        isWriting = true;
        getTaskExecutor().submitWrite(() -> {
            synchronized (this) {
                while (!bufferedOutput.isEmpty()) {
                    ByteBuffer buffer = bufferedOutput.peek();
                    try {
                        getEndpoint().write(buffer);
                    } catch (NetworkCommunicationFailedException e) {
                        disconnect();
                        Logger.getGlobal().warn("Ошибка при записи ответа", e);

                        return;
                    } catch (EndpointDisconnectedException e) {
                        disconnect();

                        return;
                    }

                    if (buffer.hasRemaining()) {
                        break;
                    }

                    bufferedOutput.poll();
                    try {
                        Logger.getGlobal().info("Отправлен ответ клиенту по адресу "
                                + getEndpoint().getRemoteAddress());
                    } catch (NetworkCommunicationFailedException | EndpointDisconnectedException e) {
                        Logger.getGlobal().info("Отправлен ответ клиенту");
                    }
                }

                isWriting = false;
            }
        });
    }

    /**
     * Получает запрос от клиента.
     *
     * @return запрос от клиента
     */
    public synchronized NetworkRequest pollRequest() throws UnauthorizedRequestException {
        if (isDisconnected()) {
            throw new EndpointDisconnectedException();
        }

        try {
            return pollStrategy.poll(this);
        } catch (UnauthorizedRequestException e) {
            disconnect();

            throw e;
        }
    }

    /**
     * Добавляет ответ, который должен быть отправлен клиенту.
     *
     * @param response Ответ клиенту
     */
    public synchronized void offerResponse(NetworkResponse response) throws EndpointDisconnectedException  {
        if (isDisconnected()) {
            throw new EndpointDisconnectedException();
        }

        try {
            ByteBuffer buffer = CustomSerializer.serialize(response);

            bufferedOutput.offer(buffer);
        } catch (IOException e) {
            disconnect();
        }
    }

    /**
     * @return {@code true}, если соединение разорвано
     */
    public synchronized boolean isDisconnected() {
        return isDisconnected;
    }

    /**
     * @return {@code true}, если присутствует необработанный запрос от клиента
     */
    public synchronized boolean hasPendingRequest() {
        return getState() instanceof HasPendingRequestState;
    }

    /**
     * @return исполнитель задач чтения-записи
     */
    public synchronized TaskExecutor getTaskExecutor() {
        return executor;
    }

    /**
     * @return конечную точку на стороне сервера
     */
    public synchronized Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @return текущее состояние соединения.
     */
    public synchronized ReadLoopState getState() {
        return state;
    }

    /**
     * @return текущее стратегию получения запроса.
     */
    public synchronized PollStrategy getPollStrategy() {
        return pollStrategy;
    }

    /**
     * Устанавливает стратегию получения запроса.
     *
     * @param pollStrategy стратегия получения запроса
     */
    public synchronized void setPollStrategy(PollStrategy pollStrategy) {
        this.pollStrategy = pollStrategy;
    }

    /**
     * Устанавливает состояние соединения.
     *
     * @param state Новое состояние
     */
    public synchronized void setState(ReadLoopState state) {
        this.state = state;
    }

    /**
     * Разрывает соединение с клиентом.
     */
    public synchronized void disconnect() {
        isDisconnected = true;
    }

    @Override
    public void close() throws IOException {
        endpoint.close();
    }
}
