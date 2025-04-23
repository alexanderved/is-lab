package client.controller.user;

import client.controller.Runner;
import client.exceptions.AuthFailedException;
import common.domain.User;
import common.exceptions.InvalidFieldFormatException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;

public abstract class UnauthorizedUser extends User {
    public UnauthorizedUser(String name, String password) throws InvalidFieldFormatException {
        super(name, password);
    }

    public abstract User auth(Runner runner)
            throws NetworkCommunicationFailedException, AuthFailedException,
            UserNameAlreadyTakenException, UserNotFoundException;
}
