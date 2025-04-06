package server.controller.executors;

import java.util.List;

import common.domain.Organization;
import common.network.requests.FilterStartsWithFullNameRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.FilteredShowResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class FilterStartsWithFullNameRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.FILTER_STARTS_WITH_FULL_NAME.getName())
                || !(req instanceof FilterStartsWithFullNameRequest)) {
            throw new UnsupportedRequestException(req);
        }

        FilterStartsWithFullNameRequest filteredReq = (FilterStartsWithFullNameRequest) req;
        List<Organization> filteredOrgs = runner.getCollectionManager()
                .getSortedCollection()
                .stream()
                .filter((org) -> org.getFullName() != null
                        && org.getFullName().startsWith(filteredReq.getFullName()))
                .toList();

        return new FilteredShowResponse(filteredOrgs);
    }
}
