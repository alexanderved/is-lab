package common.network.requests;

import common.utils.CommandInfo;

public class AverageOfEmployeesCountRequest extends NetworkRequest {
    public AverageOfEmployeesCountRequest() {
        super(CommandInfo.AVERAGE_OF_EMPLOYEES_COUNT.getName());
    }
}
