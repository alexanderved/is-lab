package server.exceptions;

/**
 * Исключение, выбрасываемое в том случае, когда не удалось произвести действие над репозиторием.
 */
public class RepositoryAccessFailedException extends Exception {
    /**
     * @param message Сообщение о возникшей ошибке
     */
    public RepositoryAccessFailedException(String message) {
        super(message);
    }

    /**
     * @param e Ошибка или исключение, из-за которого не удалось произвести действие над репозиторием
     */
    public RepositoryAccessFailedException(Throwable e) {
        super(e.getMessage(), e);
    }

    @Override
    public String getMessage() {
        return "Ошибка работы с репозиторием: " + super.getMessage();
    }
}