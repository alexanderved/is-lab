package common.network.responses;

/**
 * Ответ от сервера о неудачной обработке запроса.
 */
public class FailureResponse extends NetworkResponse {
    /**
     * @param msg сообщение о неудаче
     */
    public FailureResponse(String msg) {
        super(msg);
    }
}
