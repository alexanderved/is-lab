package server.controller.executors;

import common.exceptions.UserNotFoundException;
import common.network.requests.NetworkRequest;
import common.network.responses.AuthResponse;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.network.responses.UserNotFoundResponse;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;
import server.exceptions.UserDBAccessFailedException;
import server.network.connections.pollstrategies.AuthorizedPollStrategy;
import server.network.connections.requests.LoginServerRequest;

public class LoginRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals("login") || !(req instanceof LoginServerRequest loginReq)) {
            throw new UnsupportedRequestException(req);
        }

        try {
            runner.getUserManager().loginUser(loginReq.getLoginRequest().getUser());
            loginReq.getConnection().setPollStrategy(new AuthorizedPollStrategy());

            return new AuthResponse();
        } catch (UserNotFoundException e) {
            return new UserNotFoundResponse();
        } catch (UserDBAccessFailedException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}
