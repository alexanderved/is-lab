package server.controller.executors;

import common.network.requests.ClearRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class ClearRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.CLEAR.getName())
                || !(req instanceof ClearRequest)) {
            throw new UnsupportedRequestException(req);
        }

        return new SuccessResponse("Коллекция очищена");
    }
}
