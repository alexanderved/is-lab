package server.controller;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import server.controller.executors.RequestExecutor;
import server.exceptions.UnsupportedRequestException;
import server.network.connections.ClientConnection;
import server.network.providers.ConnectionProvider;

public class ClientResponder {
    private ConnectionProvider provider;
    private RequestExecutor reqExecutor;

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

    public void respond(Runner runner) throws NetworkCommunicationFailedException {
        var it = provider.provide().iterator();
        while (it.hasNext()) {
            ClientConnection c = it.next();

            NetworkRequest req = c.pollRequest();
            try {
                NetworkResponse res = reqExecutor.execute(req, runner);
                server.Main.logger.info("Выполнен запрос `" + req.getName() + "`");

                c.offerResponse(res);
            } catch (UnsupportedRequestException reqException) {
                server.Main.logger.warning("В процессе выполнения запроса `" + req.getName()
                        + "` произошла ошибка");

                c.offerResponse(new ErrorResponse(reqException.getMessage()));
            }

            it.remove();
        }
    }
}
