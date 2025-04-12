package server.exceptions;

public class IDGeneratorException extends RuntimeException {
    public IDGeneratorException(String message, Throwable e) {
        super(message, e);
    }
}
