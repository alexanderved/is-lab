package server.service.user;

import java.util.ArrayList;
import java.util.List;

public class UserTemplate {
    private String username;
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
