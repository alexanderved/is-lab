package server.exceptions;

public class InvalidFieldFormatException extends RuntimeException {
    public InvalidFieldFormatException() {
        super("Введенное поле имеет неверный формат");
    }

    public InvalidFieldFormatException(String fieldName) {
        super("Введенное поле `" + fieldName + "` имеет неверный формат");
    }

    public InvalidFieldFormatException(String fieldName, String reason) {
        super("Введенное поле `" + fieldName + "` имеет неверный формат: " + reason);
    }

    public InvalidFieldFormatException(Throwable e) {
        super(e.getMessage(), e);
    }
}