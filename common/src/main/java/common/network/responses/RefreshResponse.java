package common.network.responses;

public class RefreshResponse extends SuccessResponse {
    private final String access;
    private final String refresh;

    public RefreshResponse(String access, String refresh) {
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

