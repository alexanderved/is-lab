package server.controller;

import java.util.logging.Level;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import server.application.CollectionManager;
import server.controller.executors.RequestExecutor;
import server.exceptions.UnsupportedRequestException;
import server.network.connections.ClientConnection;
import server.network.providers.ConnectionProvider;

/**
 * Обработчик запросов к серверу.
 */
public class Runner {
    private State state = State.STOPPED;
    private ConnectionProvider provider;
    private RequestExecutor reqExecutor;
    private CollectionManager collectionManager;

    public Runner(ConnectionProvider provider, RequestExecutor reqExecutor, CollectionManager collectionManager) {
        if (provider == null) {
            throw new IllegalArgumentException(
                    "`provider` в классе `Runner` не должно быть null");
        }
        this.provider = provider;

        if (reqExecutor == null) {
            throw new IllegalArgumentException(
                    "`reqExecutor` в классе `Runner` не должно быть null");
        }
        this.reqExecutor = reqExecutor;

        if (collectionManager == null) {
            throw new IllegalArgumentException(
                    "`collectionManager` в классе `Runner` не должно быть null");
        }
        this.collectionManager = collectionManager;
    }

    /**
     * @return поставщик соединений с клиентами, от которых поступают запросы
     * @see ConnectionProvider
     */
    public ConnectionProvider getConnectionProvider() {
        return provider;
    }

    /**
     * Устанавливает поставщик соединений с клиентами, от которых поступают запросы
     * @param provider поставщик соединений с клиентами, от которых поступают запросы
     * @see ConnectionProvider
     */
    public void setConnectionProvider(ConnectionProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException(
                    "`provider` в классе `Runner` не должно быть null");
        }

        this.provider = provider;
    }

    /**
     * @return исполнитель запросов к серверу
     */
    public RequestExecutor getRequestExecutor() {
        return reqExecutor;
    }

    /**
     * Устанавливает исполнитель запросов к серверу.
     * @param reqExecutor Исполнитель запросов к серверу
     */
    public void setRequestExecutor(RequestExecutor reqExecutor) {
        if (reqExecutor == null) {
            throw new IllegalArgumentException(
                    "`reqExecutor` в классе `Runner` не должно быть null");
        }
        this.reqExecutor = reqExecutor;
    }

    /**
     * @return менеджер коллекции организаций
     * @see CollectionManager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Устанавливает менеджер коллекции организаций
     * @param collectionManager менеджер коллекции организаций
     */
    public void setCollectionManager(CollectionManager collectionManager) {
        if (collectionManager == null) {
            throw new IllegalArgumentException(
                    "`collectionManager` в классе `Runner` не должно быть null");
        }
        this.collectionManager = collectionManager;
    }

    /**
     * Цикл исполнения программы.
     */
    public void run() {
        setState(State.RUNNING);

        while (getState() == State.RUNNING) {
            try {
                var it = provider.provide().iterator();
                while (it.hasNext()) {
                    ClientConnection c = it.next();

                    NetworkRequest req = c.pollRequest();
                    try {
                        NetworkResponse res = reqExecutor.execute(req, this);
                        server.Main.logger.info("Выполнен запрос `" + req.getName() + "`");

                        c.offerResponse(res);
                    } catch (UnsupportedRequestException reqException) {
                        server.Main.logger.warning("В процессе выполнения запроса `" + req.getName()
                                + "` произошла ошибка");

                        c.offerResponse(new ErrorResponse(reqException.getMessage()));
                    }

                    it.remove();
                }
            } catch (NetworkCommunicationFailedException e) {
                server.Main.logger.log(Level.SEVERE, e.toString(), e);

                stop();
            }
        }
    }

    /**
     * Останавливает выполнение программы.
     */
    public void stop() {
        setState(State.STOPPED);
    }

    private State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }

    private enum State {
        STOPPED, RUNNING;
    }
}