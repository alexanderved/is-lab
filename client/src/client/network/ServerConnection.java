package client.network;

import client.exceptions.ServerRequestFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;

public abstract class ServerConnection {
    public abstract NetworkResponse request(NetworkRequest req) throws ServerRequestFailedException;
}
