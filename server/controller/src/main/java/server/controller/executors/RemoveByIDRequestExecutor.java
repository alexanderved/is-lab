package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.requests.RemoveByIDRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.FailureResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnsupportedRequestException;

public class RemoveByIDRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.REMOVE_BY_ID.getName()) || !(req instanceof RemoveByIDRequest)) {
            throw new UnsupportedRequestException(req);
        }

        RemoveByIDRequest removeReq = (RemoveByIDRequest) req;
        try {
            if (!runner.getCollectionManager().removeById(removeReq.getID())) {
                return new FailureResponse("Не удалось найти элемент с данным ID");
            }
        } catch (CollectionOperationFailedException e) {
            return new ErrorResponse("Не удалось удалить элемент с данным ID");
        }

        return new SuccessResponse("Элемент с данным ID удален");
    }
}
