package common.network.requests;

import java.io.Serializable;

import common.utils.Validatable;

public abstract class NetworkRequest implements Validatable, Serializable {
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
