package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.exceptions.UnsupportedRequestException;
import server.controller.Runner;

/**
 * Исполнитель запросов к серверу.
 */
public abstract class RequestExecutor {
    /**
     * Исполняет запрос к серверу.
     * 
     * @param req Запрос к серверу
     * @param runner Исполнитель запросов
     * @return ответ на запрос
     * @throws UnsupportedRequestException если исполнение данного запроса не поддерживается
     */
    public abstract NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException;
}
