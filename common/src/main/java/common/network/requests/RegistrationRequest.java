package common.network.requests;

public class RegistrationRequest extends NetworkRequest {
    private String username;
    private String email;
    private String firstName;
    private String secondName;
    private String password;

    private RegistrationRequest() {

    }

    public RegistrationRequest(String username, String email, String firstName,
                               String secondName, String password) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPassword() {
        return password;
    }
}
