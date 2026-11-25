package server.exceptions;

public class ImportException extends RuntimeException {
    public ImportException(String msg) {
        super(msg);
    }

    public ImportException(Throwable e) {
        super(e.getMessage(), e);
    }
}
