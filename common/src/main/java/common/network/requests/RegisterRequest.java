package common.network.requests;

import common.domain.User;

public class RegisterRequest extends AuthRequest {
    public RegisterRequest(User user) {
        super("register", user);
    }
}
