package client.controller.user;

import client.controller.Runner;
import client.exceptions.AuthFailedException;
import common.domain.User;
import common.exceptions.InvalidFieldFormatException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UserNotFoundException;
import common.network.requests.LoginRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.AuthResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.UserNotFoundResponse;

public class LoggedOutUser extends UnauthorizedUser {
    public LoggedOutUser(String name, String password) throws InvalidFieldFormatException {
        super(name, password);
    }

    @Override
    public User auth(Runner runner)
            throws NetworkCommunicationFailedException, AuthFailedException, UserNotFoundException {
        try {
            User user = new User(getName(), getPassword());

            NetworkRequest req = new LoginRequest(user);
            NetworkResponse res = runner.getServerConnection().request(req);
            if (res instanceof AuthResponse) {
                return user;
            } else if (res instanceof UserNotFoundResponse) {
                throw new UserNotFoundException();
            } else {
                throw new AuthFailedException(res.getMessage());
            }
        } catch (InvalidFieldFormatException e) {
            throw new RuntimeException("Данные пользователя изменились по неизвестной причине", e);
        }
    }
}
