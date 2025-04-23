package common.network.requests;

import common.domain.User;
import common.utils.CommandInfo;

public class RemoveByIDRequest extends NetworkRequest {
    private final User user;
    private final int id;

    public RemoveByIDRequest(User user, int id) {
        super(CommandInfo.REMOVE_BY_ID.getName());

        this.user = user;
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean isValid() {
        return user.isValid();
    }
}
