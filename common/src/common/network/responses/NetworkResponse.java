package common.network.responses;

/**
 * Ответ от сервера на запрос.
 */
public abstract class NetworkResponse {
    private String msg;

    protected NetworkResponse(String msg) {
        this.msg = msg;
    }

    /**
     * @return сообщение от сервера
     */
    public String getMessage() {
        return msg;
    }
}
