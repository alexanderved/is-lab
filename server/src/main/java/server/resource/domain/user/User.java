package server.resource.domain.user;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private final String username;
    private Role role;
    private final String passwordHash;
    private final String salt;
    private final List<String> refreshTokens;

    public User(int id, UserTemplate template) {
        this.id = id;
        username = template.getUsername();
        role = template.getRole();
        passwordHash = template.getPasswordHash();
        salt = template.getSalt();
        refreshTokens = new ArrayList<>(template.getRefreshTokens());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public List<String> getRefreshTokens() {
        return refreshTokens;
    }
}
