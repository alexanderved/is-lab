package common.network.requests;

public class LoginRequest extends NetworkRequest {
    private String username;
    private String password;

    private LoginRequest() {

    }

    public LoginRequest(String username, String password) {
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
