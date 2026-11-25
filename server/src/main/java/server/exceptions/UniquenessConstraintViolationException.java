package server.exceptions;

public class UniquenessConstraintViolationException extends RuntimeException {
    public UniquenessConstraintViolationException(String message) {
        super(message);
    }
}
