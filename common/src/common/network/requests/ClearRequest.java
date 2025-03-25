package common.network.requests;

import common.utils.CommandInfo;

public class ClearRequest extends NetworkRequest {
    public ClearRequest() {
        super(CommandInfo.CLEAR.getName());
    }
}
