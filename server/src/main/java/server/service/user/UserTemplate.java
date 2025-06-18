package server.service.user;

import java.util.ArrayList;
import java.util.List;

public class UserTemplate {
    private String username;
    private String email;
    private String firstName;
    private String secondName;
    private Role role;
    private String passwordHash;
    private String salt;
    private List<String> refreshTokens = new ArrayList<>();

    public UserTemplate() {

    }

    public String getUsername() {
        return username;
    }

    public UserTemplate setUsername(String username) {
        this.username = username;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserTemplate setEmail(String email) {
        this.email = email;

        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserTemplate setFirstName(String firstName) {
        this.firstName = firstName;

        return this;
    }

    public String getSecondName() {
        return secondName;
    }

    public UserTemplate setSecondName(String secondName) {
        this.secondName = secondName;

        return this;
    }

    public Role getRole() {
        return role;
    }

    public UserTemplate setRole(Role role) {
        this.role = role;

        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserTemplate setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;

        return this;
    }

    public String getSalt() {
        return salt;
    }

    public UserTemplate setSalt(String salt) {
        this.salt = salt;

        return this;
    }

    public List<String> getRefreshTokens() {
        return refreshTokens;
    }

    public UserTemplate setRefreshTokens(List<String> refreshTokens) {
        this.refreshTokens = refreshTokens;

        return this;
    }
}
