package server.controller;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import server.controller.executors.RequestExecutor;
import server.exceptions.UnsupportedRequestException;
import server.logger.Logger;
import server.network.connections.ClientConnection;
import server.network.providers.ConnectionProvider;

/**
 * Класс, выступающий посредником между соединениями с клиентами и исполнителями запросов.
 */
public class ClientResponder {
    private final ConnectionProvider provider;
    private final RequestExecutor reqExecutor;

    /**
     * @param provider Провайдер соединений с клиентами
     * @param reqExecutor Исполнитель запросов
     */
    public ClientResponder(ConnectionProvider provider, RequestExecutor reqExecutor) {
        if (provider == null) {
            throw new IllegalArgumentException(
                    "`provider` не должно быть null");
        }
        this.provider = provider;

        if (reqExecutor == null) {
            throw new IllegalArgumentException(
                    "`reqExecutor` не должно быть null");
        }
        this.reqExecutor = reqExecutor;
    }

    /**
     * Исполняет запросы клиентов и посылает ответы.
     *
     * @param runner Исполнитель задач сервера
     * @throws NetworkCommunicationFailedException если в процессе коммуникации возникла ошибка сети
     */
    public void respond(Runner runner) throws NetworkCommunicationFailedException {
        var it = provider.provide().iterator();
        while (it.hasNext()) {
            ClientConnection c = it.next();
            it.remove();

            NetworkRequest req = c.pollRequest();
            new Thread(() -> {
                try {
                    NetworkResponse res = reqExecutor.execute(req, runner);
                    Logger.getGlobal().info("Выполнен запрос `" + req.getName() + "`");

                    c.offerResponse(res);
                } catch (UnsupportedRequestException reqException) {
                    Logger.getGlobal().warn("В процессе выполнения запроса `" + req.getName()
                            + "` произошла ошибка");

                    c.offerResponse(new ErrorResponse(reqException.getMessage()));
                }
            }).start();
        }
    }
}
