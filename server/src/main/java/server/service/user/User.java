package server.service.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private int id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String secondName;
    private Role role;
    private final String passwordHash;
    private final String salt;
    private final List<String> refreshTokens;

    public User(int id, UserTemplate template) {
        this.id = id;
        username = template.getUsername();
        email = template.getEmail();
        firstName = template.getFirstName();
        secondName = template.getSecondName();
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole().toString()));
    }

    @Override
    public String getPassword() {
        return getPasswordHash();
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
