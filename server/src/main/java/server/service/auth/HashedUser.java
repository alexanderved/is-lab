package server.service.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class HashedUser implements UserDetails {
    private final String name;
    private final Role role;
    private final String passwordHash;
    private final String salt;
    private final List<String> refreshTokens;

    public HashedUser(String name, Role role, String passwordHash, String salt) {
        this(name, role, passwordHash, salt, new ArrayList<>());
    }

    public HashedUser(String name, Role role, String passwordHash, String salt, List<String> refreshTokens) {
        this.name = name;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.refreshTokens = refreshTokens;
    }

    @Override
    public String getUsername() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getPassword() {
        return getPasswordHash();
    }

    public String getSalt() {
        return salt;
    }

    public List<String> getRefreshTokens() {
        return refreshTokens;
    }

    public void addRefreshToken(String refreshToken) {
        this.refreshTokens.add(refreshToken);
    }

    public void removeRefreshToken(String refreshToken) {
        System.out.println("CONTAINS: " + refreshTokens.contains(refreshToken));
        System.out.println("REMOVE: " + refreshToken);
        this.refreshTokens.remove(refreshToken);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.toString()));
    }
}
