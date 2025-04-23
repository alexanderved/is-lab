package client.exceptions;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;

public class AuthFailedException extends Exception {
    public AuthFailedException(String msg) {
        super(msg);
    }

    public AuthFailedException(Throwable e) {
        super(e.toString(), e);
    }
}
