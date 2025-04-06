package common.network.responses;

/**
 * Ответ от сервера об ошибке в процессе обработки запроса.
 */
public class ErrorResponse extends FailureResponse {
    /**
     * @param msg сообщение об ошибке
     */
    public ErrorResponse(String msg) {
        super(msg);
    }
}
