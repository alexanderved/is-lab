package server.exceptions;

public class UserDBAccessFailedException extends Exception {
    public UserDBAccessFailedException(Throwable e) {
        super(e.toString(), e);
    }
}
