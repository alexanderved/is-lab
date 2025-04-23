package server.network.connections.requests;

import common.network.requests.LoginRequest;
import common.network.requests.NetworkRequest;
import server.network.connections.ClientConnection;

public class LoginServerRequest extends NetworkRequest {
    LoginRequest req;
    ClientConnection connection;

    public LoginServerRequest(LoginRequest req, ClientConnection connection) {
        super(req.getName());

        this.req = req;
        this.connection = connection;
    }

    public LoginRequest getLoginRequest() {
        return req;
    }

    public ClientConnection getConnection() {
        return connection;
    }
}
