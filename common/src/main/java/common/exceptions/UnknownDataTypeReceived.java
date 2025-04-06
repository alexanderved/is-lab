package common.exceptions;

/**
 * Исключение, выбрасываемое в том случае, когда в процессе сетевого взаимодействия
 * были получены данные неизвестного типа.
 */
public class UnknownDataTypeReceived extends Exception {
    /**
     * @param message Сообщение о возникшей ошибке
     */
    public UnknownDataTypeReceived(String message) {
        super(message);
    }

    /**
     * @param e Ошибка или исключение, возникшая при попытке прочитать полученные данные.
     */
    public UnknownDataTypeReceived(Throwable e) {
        super(e.getMessage(), e);
    }

    @Override
    public String getMessage() {
        return "Получены данные низвестного типа: " + super.getMessage();
    }
}
