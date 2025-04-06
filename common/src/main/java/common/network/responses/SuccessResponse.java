package common.network.responses;

/**
 * Ответ от сервера об успешной обработке запроса.
 */
public class SuccessResponse extends NetworkResponse {
    /**
     * @param msg сообщение об успехе
     */
    public SuccessResponse(String msg) {
        super(msg);
    }
}
