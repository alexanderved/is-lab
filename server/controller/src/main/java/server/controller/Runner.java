package server.controller;

import common.exceptions.NetworkCommunicationFailedException;
import server.application.CollectionManager;
import server.application.UserManager;
import server.logger.Logger;

/**
 * Исполнитель задач сервера.
 */
public class Runner {
    private State state = State.STOPPED;
    private final ClientResponder responder;
    private final UserManager userManager;
    private final CollectionManager collectionManager;

    public Runner(ClientResponder responder, UserManager userManager, CollectionManager collectionManager) {
        if (responder == null) {
            throw new IllegalArgumentException(
                    "`responder`не должно быть null");
        }
        this.responder = responder;

        if (userManager == null) {
            throw new IllegalArgumentException(
                    "`userManager`не должно быть null");
        }
        this.userManager = userManager;

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
     * @return менеджер пользователей
     * @see UserManager
     */
    public UserManager getUserManager() {
        return userManager;
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