package server.network.connections.requests;

import common.network.requests.NetworkRequest;
import common.network.requests.RegisterRequest;
import server.network.connections.ClientConnection;

public class RegisterServerRequest extends NetworkRequest {
    RegisterRequest req;
    ClientConnection connection;

    public RegisterServerRequest(RegisterRequest req, ClientConnection connection) {
        super(req.getName());

        this.req = req;
        this.connection = connection;
    }

    public RegisterRequest getLoginRequest() {
        return req;
    }

    public ClientConnection getConnection() {
        return connection;
    }
}
