package server.controller;

import common.exceptions.InvalidFieldFormatException;
import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.responses.LoginResponse;
import common.network.responses.RefreshResponse;
import common.network.responses.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.InvalidRefreshTokenException;
import server.service.auth.AuthService;
import server.controller.dto.UserDTO;
import server.exceptions.UserDBAccessFailedException;
import server.service.token.Token;
import server.service.token.TokenPair;
import server.service.token.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/registration")
    public SuccessResponse register(@RequestBody UserDTO user)
            throws InvalidFieldFormatException, UserDBAccessFailedException, UserNameAlreadyTakenException {
        authService.register(user.toDomain());

        return new SuccessResponse();
    }

    @PostMapping("/login")
    public SuccessResponse login(@RequestBody UserDTO user)
            throws InvalidFieldFormatException, UserDBAccessFailedException, UserNotFoundException {
        TokenPair pair = authService.login(user.toDomain());
        String access = tokenService.generateTokenString(pair.getAccess());
        String refresh = tokenService.generateTokenString(pair.getRefresh());

        return new LoginResponse(access, refresh);
    }

    @PostMapping("/refresh")
    public SuccessResponse refresh(@RequestBody String refreshTokenString)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        Token refreshToken = tokenService.parseTokenString(refreshTokenString);
        TokenPair pair = authService.refresh(refreshToken);

        String newAccess = tokenService.generateTokenString(pair.getAccess());
        String newRefresh = tokenService.generateTokenString(pair.getRefresh());

        return new RefreshResponse(newAccess, newRefresh);
    }
}
