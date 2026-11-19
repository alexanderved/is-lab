package server.service.auth;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.requests.LoginRequest;
import common.network.requests.RegistrationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.InvalidRefreshTokenException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.AUserRepository;
import server.service.token.Token;
import server.service.token.TokenPair;
import server.service.token.TokenService;
import server.service.user.Role;
import server.service.user.User;
import server.service.user.UserTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class AuthService {
    private static final Random RANDOM = new SecureRandom();

    @Inject
    private AUserRepository AUserRepository;

    @Inject
    private Hasher hasher;

    @Inject
    private TokenService tokenService;

    public AuthService() {

    }

    @Transactional
    public void registerSuperadmin(RegistrationRequest req)
            throws UserNameAlreadyTakenException, UserDBAccessFailedException {
        String salt = generateSalt();
        String passwordHash = hashPassword(req.getPassword(), salt);

        UserTemplate template = new UserTemplate()
                .setUsername(req.getUsername())
                .setRole(Role.ROLE_USER)
                .setPasswordHash(passwordHash)
                .setSalt(salt);

        try {
            AUserRepository.createSuperadmin(template);
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    @Transactional
    public void register(RegistrationRequest req)
            throws UserNameAlreadyTakenException, UserDBAccessFailedException {
        String salt = generateSalt();
        String passwordHash = hashPassword(req.getPassword(), salt);

        UserTemplate template = new UserTemplate()
                .setUsername(req.getUsername())
                .setRole(Role.ROLE_USER)
                .setPasswordHash(passwordHash)
                .setSalt(salt);

        try {
            AUserRepository.add(template);
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    @Transactional
    public TokenPair login(LoginRequest req)
            throws UserNotFoundException, UserDBAccessFailedException {
        try {
            User user = AUserRepository.findByName(req.getUsername());
            String hashedPassword = hashPassword(req.getPassword(), user.getSalt());
            if (!hashedPassword.equals(user.getPasswordHash())) {
                throw new UserNotFoundException();
            }

            TokenPair pair = tokenService.generateTokenPair(user);
            List<String> tokens = user.getRefreshTokens();
            tokens.add(tokenService.generateTokenString(pair.getRefresh()));
            AUserRepository.update(user);

            return pair;
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    @Transactional
    public TokenPair refresh(Token token)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        try {
            User user = AUserRepository.findByName(token.getUsername());
            if (!tokenService.isRefreshTokenValid(token, user)) {
                throw new InvalidRefreshTokenException();
            }

            TokenPair pair = tokenService.generateTokenPair(user);

            List<String> tokens = user.getRefreshTokens();
            tokens.remove(tokenService.generateTokenString(token));
            tokens.add(tokenService.generateTokenString(pair.getRefresh()));

            AUserRepository.update(user);

            return pair;
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        } catch (UserNotFoundException e) {
            throw new InvalidRefreshTokenException(e);
        }
    }

    private String hashPassword(String password, String salt) {
        return hasher.hash(password + salt);
    }

    private static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);

        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
