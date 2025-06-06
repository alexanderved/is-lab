package server.controller;

import common.exceptions.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import server.exceptions.MalformedTokenException;
import server.exceptions.RepositoryAccessFailedException;
import server.service.auth.AuthService;
import server.service.auth.HashedUser;
import server.service.token.Token;
import server.service.token.TokenService;
import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private final static String BEARER_PREFIX = "Bearer ";

    public final AuthService authService;
    public final TokenService tokenService;

    public TokenFilter(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenString = header.split(" ")[1].trim();
        Token token;
        HashedUser user;
        try {
            token = tokenService.parseTokenString(tokenString);
            user = authService.findByName(token.getUsername());
        } catch (UserNotFoundException | RepositoryAccessFailedException | MalformedTokenException e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!tokenService.isAccessTokenValid(token, user)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
