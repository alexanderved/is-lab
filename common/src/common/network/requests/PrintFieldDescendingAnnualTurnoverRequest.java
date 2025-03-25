package common.network.requests;

import common.utils.CommandInfo;

public class PrintFieldDescendingAnnualTurnoverRequest extends NetworkRequest {
    public PrintFieldDescendingAnnualTurnoverRequest() {
        super(CommandInfo.PRINT_FIELD_DESCENDING_ANNUAL_TURNOVER.getName());
    }
}
