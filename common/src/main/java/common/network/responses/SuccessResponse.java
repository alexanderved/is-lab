package common.network.responses;

/**
 * Ответ от сервера об успешной обработке запроса.
 */
public class SuccessResponse extends NetworkResponse {
    public SuccessResponse() {
        super("OK");
    }
}
