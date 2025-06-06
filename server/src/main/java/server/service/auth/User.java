package server.service.auth;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails, Serializable, Validatable {
    private final String username;
    private final Role role;
    private final String password;

    public User(String username, Role role, String password) throws InvalidFieldFormatException {
        if (username == null || username.isBlank()) {
            throw new InvalidFieldFormatException("username");
        }
        this.username = username;

        if (role == null) {
            throw new InvalidFieldFormatException("role");
        }
        this.role = role;

        if (password == null) {
            throw new InvalidFieldFormatException("password");
        }
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.ROLE_USER.toString()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isValid() {
        return username != null && !username.isBlank() && password != null;
    }
}
