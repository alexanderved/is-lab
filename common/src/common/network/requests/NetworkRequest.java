package common.network.requests;

import common.utils.Validatable;

public abstract class NetworkRequest implements Validatable {
    private final String name;

    protected NetworkRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
