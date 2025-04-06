package server.controller.executors;

import common.network.requests.InfoRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.InfoResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.application.CollectionManager;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class InfoRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.INFO.getName())
                || !(req instanceof InfoRequest)) {
            throw new UnsupportedRequestException(req);
        }

        CollectionManager manager = runner.getCollectionManager();
        
        return new InfoResponse(manager.getCollectionType(), manager.getCollectionSize(),
                manager.getInitDate(), manager.getLastModDate());
    }
}
