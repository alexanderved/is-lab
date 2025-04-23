package server.controller.executors;

import common.network.requests.AddRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.AddResponse;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnsupportedRequestException;

public class AddRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.ADD.getName()) || !(req instanceof AddRequest)) {
            throw new UnsupportedRequestException(req);
        }

        AddRequest addReq = (AddRequest) req;
        try {
            int newId = runner.getCollectionManager().add(addReq.getUser(), addReq.getOrganizationTemplate());

            return new AddResponse(newId);
        } catch (CollectionOperationFailedException e) {
            return new ErrorResponse("Не удалось добавить организацию");
        }
    }
}
