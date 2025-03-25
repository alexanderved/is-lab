package common.network.requests;

import common.utils.CommandInfo;

public class ShowRequest extends NetworkRequest {
    public ShowRequest() {
        super(CommandInfo.SHOW.getName());
    }
}
