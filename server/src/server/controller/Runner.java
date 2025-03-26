package server.controller;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.responses.ErrorResponse;
import server.application.CollectionManager;
import server.controller.executors.RequestExecutor;
import server.exceptions.UnsupportedRequestException;
import server.network.connections.Connection;

/**
 * Обработчик запросов к серверу.
 */
public class Runner {
    private State state = State.STOPPED;
    private Connection connection;
    private RequestExecutor reqExecutor;
    private CollectionManager collectionManager;

    public Runner(Connection connection, RequestExecutor reqExecutor, CollectionManager collectionManager) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "`connection` в классе `Runner` не должно быть null");
        }
        this.connection = connection;

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
     * @return соединение с клиентами, от которых поступают запросы
     * @see Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Устанавливает соединение с клиентами, от которых поступают запросы
     * @param connection соединение с клиентами, от которых поступают запросы
     * @see Connection
     */
    public void setConnection(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "`connection` в классе `Runner` не должно быть null");
        }

        this.connection = connection;
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
                connection.respond((req) -> {
                    try {
                        return reqExecutor.execute(req, this);
                    } catch (UnsupportedRequestException reqException) {
                        return new ErrorResponse(reqException.getMessage());
                    }
                });
            } catch (NetworkCommunicationFailedException e) {
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