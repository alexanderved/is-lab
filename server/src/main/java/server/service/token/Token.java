package server.service.token;

import server.resource.domain.user.Role;

import java.util.Date;

public class Token {
    private final String issuer;
    private final String username;
    private final Role role;
    private final Type type;
    private final Date creationDate;
    private final Date expirationDate;

    public Token(String issuer, String username, Role role, Type type,
                 Date creationDate, Date expirationDate) {
        this.issuer = issuer;
        this.username = username;
        this.role = role;
        this.type = type;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public Type getType() {
        return type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public enum Type {
        ACCESS,
        REFRESH
    }
}
