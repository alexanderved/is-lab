package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.requests.PrintFieldDescendingAnnualTurnoverRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.PrintFieldDescendingAnnualTurnoverResponse;
import common.utils.CommandInfo;
import server.application.CollectionManager;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class PrintFieldDescendingAnnualTurnoverRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.PRINT_FIELD_DESCENDING_ANNUAL_TURNOVER.getName())
                || !(req instanceof PrintFieldDescendingAnnualTurnoverRequest)) {
            throw new UnsupportedRequestException(req);
        }

        CollectionManager manager = runner.getCollectionManager();
        
        return new PrintFieldDescendingAnnualTurnoverResponse(manager.getAnnualTurnovers());
    }
}
