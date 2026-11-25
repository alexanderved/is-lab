package server.controller;

import server.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import server.exceptions.RepositoryAccessFailedException;
import server.resource.domain.user.User;
import server.service.token.Token;
import server.service.token.TokenService;
import server.service.UserService;

import java.util.Set;

@ApplicationScoped
public class JWTAuthMechanism implements HttpAuthenticationMechanism {
    @Inject
    private TokenService tokenService;

    @Inject
    private UserService userService;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context)
            throws AuthenticationException {
        addCORS(response);

        System.out.println(request.getMethod() + " " + request.getRequestURI());

        if (!context.isProtected()) {
            return context.doNothing();
        }

        if (request.getMethod().equals("OPTIONS")) {
            return context.doNothing();
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return context.responseUnauthorized();
        }

        System.out.println(authorizationHeader);

        String tokenString = authorizationHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        User user = null;
        try {
            user = userService.findByName(token.getUsername());
        } catch (UserNotFoundException e) {
            return context.responseUnauthorized();
        }

        if (!tokenService.isAccessTokenValid(token, user)) {
            return context.responseUnauthorized();
        }

        Set<String> roles = switch (user.getRole()) {
            case ROLE_USER -> Set.of("user");
            case ROLE_ADMIN -> Set.of("user", "admin");
            case ROLE_SUPERADMIN -> Set.of("user", "admin", "superadmin");
            default -> Set.of();
        };

        return context.notifyContainerAboutLogin(user.getUsername(), roles);
    }

    private static void addCORS(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "1209600");
    }
}
