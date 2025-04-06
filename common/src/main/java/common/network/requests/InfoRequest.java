package common.network.requests;

import common.utils.CommandInfo;

public class InfoRequest extends NetworkRequest {
    public InfoRequest() {
        super(CommandInfo.INFO.getName());
    }
}
