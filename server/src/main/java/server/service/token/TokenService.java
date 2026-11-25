package server.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import server.exceptions.MalformedTokenException;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TokenService {
    private static final int MIN_SECRET_BYTES = 32;

    private static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(5);
    private static final Duration REFRESH_EXPIRATION_TIME = Duration.ofDays(30);
    private static final String ISSUER = "is-lab";

    @Inject
    @ConfigProperty(name = "jwt.secret")
    String secret;

    private SecretKey key;

    public TokenService() {

    }

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Property 'jwt.secret' is not set.");
        }

        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "Property 'jwt.secret' must be at least " + MIN_SECRET_BYTES
                            + " bytes (got " + bytes.length + ").");
        }

        this.key = Keys.hmacShaKeyFor(bytes);
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
