package server.controller.executors;

import common.network.requests.AverageOfEmployeesCountRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.AverageOfEmployeesCountResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class AverageOfEmployeesCountRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.AVERAGE_OF_EMPLOYEES_COUNT.getName())
                || !(req instanceof AverageOfEmployeesCountRequest)) {
            throw new UnsupportedRequestException(req);
        }

        return new AverageOfEmployeesCountResponse(runner.getCollectionManager().averageOfEmployeesCount());
    }
}
