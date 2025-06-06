package common.network.responses;

public class LoginResponse extends SuccessResponse {
    private final String access;
    private final String refresh;

    public LoginResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}

