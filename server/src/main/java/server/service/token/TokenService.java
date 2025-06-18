package server.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import server.exceptions.MalformedTokenException;
import server.service.user.Role;
import server.service.user.User;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private static final String SECRET =
            "d2Zza2VqeG93bmdwa3drbWFzbGtma3NhbWZsa3NrYWxrZm1rbGFmbWtsYWZsa2Fz";
    private static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(5);
    private static final Duration REFRESH_EXPIRATION_TIME = Duration.ofDays(30);
    private static final String ISSUER = "proglab";

    private final SecretKey key;

    public TokenService() {
        key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public TokenPair generateTokenPair(User user) {
        Date creationDate = new Date();
        Date accessExpirationDate = new Date(creationDate.getTime() + ACCESS_EXPIRATION_TIME.toMillis());
        Date refreshExpirationDate = new Date(creationDate.getTime() + REFRESH_EXPIRATION_TIME.toMillis());

        return new TokenPair(ISSUER, user.getUsername(), Role.ROLE_USER, creationDate,
                accessExpirationDate, refreshExpirationDate);
    }

    public String generateTokenString(Token token) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", token.getRole());
        claims.put("type", token.getType());

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(token.getUsername())
                .issuedAt(token.getCreationDate())
                .expiration(token.getExpirationDate())
                .claims()
                .add(claims)
                .and()
                .signWith(key)
                .compact();
    }

    public Token parseTokenString(String tokenString) {
        Claims claims = extractClaims(tokenString);

        try {
            String issuer = claims.getIssuer();
            String username = claims.getSubject();
            Role role = Role.valueOf(claims.get("role", String.class));
            Token.Type type = Token.Type.valueOf(claims.get("type", String.class));
            Date creationDate = claims.getIssuedAt();
            Date expirationDate = claims.getExpiration();

            if (issuer == null || username == null || creationDate == null || expirationDate == null) {
                throw new MalformedTokenException(new NullPointerException());
            }

            return new Token(issuer, username, role, type, creationDate, expirationDate);
        } catch (NullPointerException e) {
            throw new MalformedTokenException(e);
        }
    }

    public boolean isAccessTokenValid(Token token, User user) {
        return token.getType() == Token.Type.ACCESS && token.getIssuer().equals(ISSUER)
                && token.getExpirationDate().after(new Date())
                && token.getUsername().equals(user.getUsername());
    }

    public boolean isRefreshTokenValid(Token token, User user) {
        return token.getType() == Token.Type.REFRESH && token.getIssuer().equals(ISSUER)
                && token.getExpirationDate().after(new Date())
                && token.getUsername().equals(user.getUsername())
                && user.getRefreshTokens().contains(generateTokenString(token));
    }

    private Claims extractClaims(String tokenString) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(tokenString)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new MalformedTokenException(e);
        }
    }
}
