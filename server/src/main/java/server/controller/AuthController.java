package server.controller;

import server.exceptions.UserNameAlreadyTakenException;
import server.exceptions.UserNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.InvalidRefreshTokenException;
import server.exceptions.UserDBAccessFailedException;
import server.resource.dto.ErrorInfo;
import server.resource.dto.TokenStringPair;
import server.resource.dto.UserCredentials;
import server.service.auth.AuthService;
import server.service.token.Token;
import server.service.token.TokenPair;
import server.service.token.TokenService;

import java.io.Serializable;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    @Inject
    private AuthService authService;

    @Inject
    private TokenService tokenService;

    public AuthController() {

    }

    @POST
    @Path("/registration")
    public Response registration(UserCredentials req)
            throws UserDBAccessFailedException, UserNameAlreadyTakenException {
        authService.register(req);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    public Response login(UserCredentials req)
            throws UserDBAccessFailedException, UserNotFoundException {
        TokenPair pair = authService.login(req);
        String access = tokenService.generateTokenString(pair.getAccess());
        String refresh = tokenService.generateTokenString(pair.getRefresh());

        return Response.ok(new TokenStringPair(access, refresh)).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(RefreshTokenString body)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        if (body == null || body.refreshToken() == null || body.refreshToken().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("refreshToken is required"))
                    .build();
        }

        Token refreshToken = tokenService.parseTokenString(body.refreshToken());
        TokenPair pair = authService.refresh(refreshToken);

        String newAccess = tokenService.generateTokenString(pair.getAccess());
        String newRefresh = tokenService.generateTokenString(pair.getRefresh());

        return Response.ok(new TokenStringPair(newAccess, newRefresh)).build();
    }

    public record RefreshTokenString(String refreshToken) implements Serializable {
    }
}