package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.requests.UpdateRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class UpdateRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.UPDATE.getName()) || !(req instanceof UpdateRequest)) {
            throw new UnsupportedRequestException(req);
        }

        UpdateRequest updateReq = (UpdateRequest) req;
        runner.getCollectionManager().update(updateReq.getID(), updateReq.getOrganizationTemplate());

        return new SuccessResponse("Данные организации с данным ID обновлены");
    }
}
