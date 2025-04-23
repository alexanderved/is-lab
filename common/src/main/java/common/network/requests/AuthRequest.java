package common.network.requests;

import common.domain.User;

public abstract class AuthRequest extends NetworkRequest {
    private final User user;

    protected AuthRequest(String name, User user) {
        super(name);

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean isValid() {
        return user.isValid();
    }
}
