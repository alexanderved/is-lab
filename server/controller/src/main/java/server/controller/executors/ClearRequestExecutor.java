package server.controller.executors;

import common.network.requests.ClearRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnsupportedRequestException;

public class ClearRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.CLEAR.getName())
                || !(req instanceof ClearRequest)) {
            throw new UnsupportedRequestException(req);
        }

        try {
            runner.getCollectionManager().clear();
        } catch (CollectionOperationFailedException e) {
            return new ErrorResponse("Не удалось очистить коллекцию");
        }

        return new SuccessResponse("Коллекция очищена");
    }
}
