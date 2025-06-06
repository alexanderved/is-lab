package server.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {

    }

    public InvalidRefreshTokenException(Throwable e) {
        super(e.toString(), e);
    }
}
