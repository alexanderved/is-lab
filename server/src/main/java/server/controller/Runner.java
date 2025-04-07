package server.controller;

import java.util.logging.Level;

import common.exceptions.NetworkCommunicationFailedException;
import server.application.CollectionManager;
import server.logger.Logger;

/**
 * Обработчик запросов к серверу.
 */
public class Runner {
    private State state = State.STOPPED;
    private ClientResponder responder;
    private CollectionManager collectionManager;

    public Runner(ClientResponder responder, CollectionManager collectionManager) {
        if (responder == null) {
            throw new IllegalArgumentException(
                    "`responder`не должно быть null");
        }
        this.responder = responder;

        if (collectionManager == null) {
            throw new IllegalArgumentException(
                    "`collectionManager`не должно быть null");
        }
        this.collectionManager = collectionManager;
    }

    /**
     * @return менеджер коллекции организаций
     * @see CollectionManager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Цикл исполнения программы.
     */
    public void run() {
        setState(State.RUNNING);

        while (getState() == State.RUNNING) {
            try {
                responder.respond(this);
            } catch (NetworkCommunicationFailedException e) {
                Logger.getGlobal().error(e.toString(), e);

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