package common.network.requests;

import common.domain.User;
import common.utils.CommandInfo;

public class ClearRequest extends NetworkRequest {
    private final User user;

    public ClearRequest(User user) {
        super(CommandInfo.CLEAR.getName());

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
