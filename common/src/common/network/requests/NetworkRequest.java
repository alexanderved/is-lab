package common.network.requests;

public abstract class NetworkRequest {
    private final String name;

    protected NetworkRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
