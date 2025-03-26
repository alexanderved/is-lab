package client.network;

import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.network.responses.SuccessResponse;

public class FakeServerConnection extends ServerConnection {
    @Override
    public NetworkResponse request(NetworkRequest req) {
        System.out.println(req.getName());

        return new SuccessResponse("");
    }
}
