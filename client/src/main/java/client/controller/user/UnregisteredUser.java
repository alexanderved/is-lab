package client.controller.user;

import client.controller.Runner;
import client.exceptions.AuthFailedException;
import common.domain.User;
import common.exceptions.InvalidFieldFormatException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UserNameAlreadyTakenException;
import common.network.requests.NetworkRequest;
import common.network.requests.RegisterRequest;
import common.network.responses.AuthResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.UserNameAlreadyTakenResponse;

public class UnregisteredUser extends UnauthorizedUser {
    public UnregisteredUser(String name, String password) throws InvalidFieldFormatException {
        super(name, password);
    }

    @Override
    public User auth(Runner runner)
            throws NetworkCommunicationFailedException, AuthFailedException, UserNameAlreadyTakenException {
        try {
            User user = new User(getName(), getPassword());

            NetworkRequest req = new RegisterRequest(user);
            NetworkResponse res = runner.getServerConnection().request(req);
            if (res instanceof AuthResponse) {
                return user;
            } else if (res instanceof UserNameAlreadyTakenResponse) {
                throw new UserNameAlreadyTakenException(((UserNameAlreadyTakenResponse) res).getName());
            } else {
                throw new AuthFailedException(res.getMessage());
            }
        } catch (InvalidFieldFormatException e) {
            throw new RuntimeException("Данные пользователя изменились по неизвестной причине", e);
        }
    }
}
