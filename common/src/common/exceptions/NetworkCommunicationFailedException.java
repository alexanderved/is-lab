package common.exceptions;

/**
 * Исключение, выбрасываемое в том случае, когда в процессе обмена информации
 * между клиентом и сервером возникла ошибка.
 */
public class NetworkCommunicationFailedException extends Exception {
    /**
     * @param message Сообщение о возникшей ошибке
     */
    public NetworkCommunicationFailedException(String message) {
        super(message);
    }

    /**
     * @param e Ошибка или исключение, из-за возникновения которого не удалось
     *          передать данные между клиентом и сервером
     */
    public NetworkCommunicationFailedException(Throwable e) {
        super(e.getMessage(), e);
    }

    @Override
    public String getMessage() {
        return "Ошибка сетевой комуникации: " + super.getMessage();
    }
}