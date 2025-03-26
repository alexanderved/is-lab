package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.requests.RemoveLowerRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class RemoveLowerRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.REMOVE_LOWER.getName()) || !(req instanceof RemoveLowerRequest)) {
            throw new UnsupportedRequestException(req);
        }

        RemoveLowerRequest removeReq = (RemoveLowerRequest) req;
        runner.getCollectionManager().removeLower(removeReq.getOrganizationTemplate());;

        return new SuccessResponse("Удалены все организации, меньшие, чем заданная");
    }
}
