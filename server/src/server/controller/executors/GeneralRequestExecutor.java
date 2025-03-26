package server.controller.executors;

import java.util.HashMap;
import java.util.Map;

import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class GeneralRequestExecutor extends RequestExecutor {
    private Map<String, RequestExecutor> executors = new HashMap<>();

    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!executors.containsKey(req.getName())) {
            throw new UnsupportedRequestException(req);
        }

        return executors.get(req.getName()).execute(req, runner);
    }

    public void register(String reqName, RequestExecutor reqExecutor) {
        executors.put(reqName, reqExecutor);
    }
}
