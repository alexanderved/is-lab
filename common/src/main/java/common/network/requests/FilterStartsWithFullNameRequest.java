package common.network.requests;

import common.utils.CommandInfo;

public class FilterStartsWithFullNameRequest extends NetworkRequest {
    private final String fullName;

    public FilterStartsWithFullNameRequest(String fullName) {
        super(CommandInfo.FILTER_STARTS_WITH_FULL_NAME.getName());

        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
