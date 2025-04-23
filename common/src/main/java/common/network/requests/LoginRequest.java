package common.network.requests;

import common.domain.User;

public class LoginRequest extends AuthRequest {
    public LoginRequest(User user) {
        super("login", user);
    }
}
