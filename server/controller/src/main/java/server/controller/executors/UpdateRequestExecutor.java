package server.controller.executors;

import common.domain.OrganizationTemplate;
import common.network.requests.NetworkRequest;
import common.network.requests.UpdateRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.FailureResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnsupportedRequestException;

public class UpdateRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.UPDATE.getName()) || !(req instanceof UpdateRequest)) {
            throw new UnsupportedRequestException(req);
        }

        UpdateRequest updateReq = (UpdateRequest) req;
        int id = updateReq.getID();
        OrganizationTemplate template = updateReq.getOrganizationTemplate();

        try {
            if (!runner.getCollectionManager().update(id, template)) {
                return new FailureResponse("Не удалось найти элемент с данным ID");
            }
        } catch (CollectionOperationFailedException e) {
            return new ErrorResponse("Не удалось обновить элемент с данным ID");
        }

        return new SuccessResponse("Данные организации с данным ID обновлены");
    }
}
