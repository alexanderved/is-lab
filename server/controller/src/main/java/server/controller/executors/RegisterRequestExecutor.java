package server.controller.executors;

import common.exceptions.UserNameAlreadyTakenException;
import common.network.requests.NetworkRequest;
import common.network.responses.*;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;
import server.exceptions.UserDBAccessFailedException;
import server.network.connections.pollstrategies.AuthorizedPollStrategy;
import server.network.connections.requests.RegisterServerRequest;

public class RegisterRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals("register") || !(req instanceof RegisterServerRequest regReq)) {
            throw new UnsupportedRequestException(req);
        }

        try {
            runner.getUserManager().registerUser(regReq.getLoginRequest().getUser());
            regReq.getConnection().setPollStrategy(new AuthorizedPollStrategy());

            return new AuthResponse();
        } catch (UserNameAlreadyTakenException e) {
            return new UserNameAlreadyTakenResponse(regReq.getName());
        } catch (UserDBAccessFailedException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}
