package server.service.token;

import server.service.auth.Role;

import java.util.Date;

public class TokenPair {
    private final Token access;
    private final Token refresh;

    public TokenPair(String issuer, String username, Role role,
                 Date creationDate, Date accessExpirationDate, Date refreshExpirationDate) {
        access = new Token(issuer, username, role, Token.Type.ACCESS, creationDate, accessExpirationDate);
        refresh = new Token(issuer, username, role, Token.Type.REFRESH, creationDate, refreshExpirationDate);
    }

    public TokenPair(Token access, Token refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public Token getAccess() {
        return access;
    }

    public Token getRefresh() {
        return refresh;
    }
}
