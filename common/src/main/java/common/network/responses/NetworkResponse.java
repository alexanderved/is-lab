package common.network.responses;

import java.io.Serializable;

/**
 * Ответ от сервера на запрос.
 */
public abstract class NetworkResponse implements Serializable {
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
