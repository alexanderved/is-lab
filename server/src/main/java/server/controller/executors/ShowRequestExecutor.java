package server.controller.executors;

import common.network.requests.NetworkRequest;
import common.network.requests.ShowRequest;
import common.network.responses.FilteredShowResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class ShowRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.SHOW.getName())
                || !(req instanceof ShowRequest)) {
            throw new UnsupportedRequestException(req);
        }

        return new FilteredShowResponse(runner.getCollectionManager().getSortedCollection());
    }
}
