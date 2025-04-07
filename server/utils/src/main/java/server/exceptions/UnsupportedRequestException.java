package server.exceptions;

import common.network.requests.NetworkRequest;

/**
 * Исключение, выбрасываемое в том случае, когда сервер получил запрос неизвестного типа.
 */
public class UnsupportedRequestException extends Exception {
    private NetworkRequest req;

    /**
     * @param req Запрос, полученный сервером
     */
    public UnsupportedRequestException(NetworkRequest req) {
        this.req = req;
    }

    @Override
    public String getMessage() {
        return "Запрос типа `" + req.getName() + "` не поддерживается";
    }
}