package server.exceptions;

public class CollectionOperationFailedException extends Exception {
    public CollectionOperationFailedException(Throwable e) {
        super(e.toString(), e);
    }
}
