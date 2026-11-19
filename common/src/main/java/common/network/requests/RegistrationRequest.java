package common.network.requests;

public class RegistrationRequest extends NetworkRequest {
    private String username;
    private String password;

    private RegistrationRequest() {

    }

    public RegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
