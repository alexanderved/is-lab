package common.network.requests;

import common.utils.CommandInfo;

public class RemoveByIDRequest extends NetworkRequest {
    private final int id;

    public RemoveByIDRequest(int id) {
        super(CommandInfo.REMOVE_BY_ID.getName());

        this.id = id;
    }

    public int getID() {
        return id;
    }
}
