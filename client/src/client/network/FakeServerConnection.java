package client.network;

import client.exceptions.ServerRequestFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;

public class FakeServerConnection extends ServerConnection {
    @Override
    public NetworkResponse request(NetworkRequest req) throws ServerRequestFailedException {
        System.out.println(req.getName());

        return new SuccessResponse("");
    }
}
