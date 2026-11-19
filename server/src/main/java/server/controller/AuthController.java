package server.controller;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.requests.LoginRequest;
import common.network.requests.RegistrationRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.LoginResponse;
import common.network.responses.RefreshResponse;
import common.network.responses.SuccessResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.InvalidRefreshTokenException;
import server.exceptions.UserDBAccessFailedException;
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
    public Response registration(RegistrationRequest req)
            throws UserDBAccessFailedException, UserNameAlreadyTakenException {
        authService.register(req);
        return Response.ok(new SuccessResponse()).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest req)
            throws UserDBAccessFailedException, UserNotFoundException {
        TokenPair pair = authService.login(req);
        String access = tokenService.generateTokenString(pair.getAccess());
        String refresh = tokenService.generateTokenString(pair.getRefresh());

        return Response.ok(new LoginResponse(access, refresh)).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(RefreshRequestDTO body)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        if (body == null || body.refreshToken == null || body.refreshToken.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("refreshToken is required"))
                    .build();
        }

        Token refreshToken = tokenService.parseTokenString(body.refreshToken);
        TokenPair pair = authService.refresh(refreshToken);

        String newAccess = tokenService.generateTokenString(pair.getAccess());
        String newRefresh = tokenService.generateTokenString(pair.getRefresh());

        return Response.ok(new RefreshResponse(newAccess, newRefresh)).build();
    }

    public static final class RefreshRequestDTO implements Serializable {
        public String refreshToken;
    }
}