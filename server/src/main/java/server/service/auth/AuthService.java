package server.service.auth;

import jakarta.enterprise.context.RequestScoped;
import server.exceptions.UserNameAlreadyTakenException;
import server.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.InvalidRefreshTokenException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.UserRepository;
import server.resource.dto.UserCredentials;
import server.service.token.Token;
import server.service.token.TokenPair;
import server.service.token.TokenService;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;
import server.resource.domain.user.UserTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@RequestScoped
public class AuthService {
    private static final Random RANDOM = new SecureRandom();

    @Inject
    private UserRepository userRepository;

    @Inject
    private Hasher hasher;

    @Inject
    private TokenService tokenService;

    public AuthService() {

    }

    @Transactional
    public void registerSuperadmin(UserCredentials req)
            throws UserNameAlreadyTakenException, UserDBAccessFailedException {
        String salt = generateSalt();
        String passwordHash = hashPassword(req.password(), salt);

        UserTemplate template = new UserTemplate()
                .setUsername(req.username())
                .setRole(Role.ROLE_USER)
                .setPasswordHash(passwordHash)
                .setSalt(salt);

        userRepository.createSuperadmin(template);
    }

    @Transactional
    public void register(UserCredentials req)
            throws UserNameAlreadyTakenException, UserDBAccessFailedException {
        String salt = generateSalt();
        String passwordHash = hashPassword(req.password(), salt);

        UserTemplate template = new UserTemplate()
                .setUsername(req.username())
                .setRole(Role.ROLE_USER)
                .setPasswordHash(passwordHash)
                .setSalt(salt);

        userRepository.add(template);
    }

    @Transactional
    public TokenPair login(UserCredentials req)
            throws UserNotFoundException, UserDBAccessFailedException {
        User user = userRepository.findByName(req.username());
        String hashedPassword = hashPassword(req.password(), user.getSalt());

        System.out.println(req.username() + ": " + req.password());
        System.out.println(req.username() + ": " + hashedPassword);
        System.out.println(req.username() + ": " + user.getPasswordHash());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            throw new UserNotFoundException(req.username());
        }

        TokenPair pair = tokenService.generateTokenPair(user);
        List<String> tokens = user.getRefreshTokens();
        tokens.add(tokenService.generateTokenString(pair.getRefresh()));
        userRepository.update(user);

        return pair;
    }

    @Transactional
    public TokenPair refresh(Token token)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        try {
            User user = userRepository.findByName(token.getUsername());
            if (!tokenService.isRefreshTokenValid(token, user)) {
                throw new InvalidRefreshTokenException();
            }

            TokenPair pair = tokenService.generateTokenPair(user);

            List<String> tokens = user.getRefreshTokens();
            tokens.remove(tokenService.generateTokenString(token));
            tokens.add(tokenService.generateTokenString(pair.getRefresh()));

            userRepository.update(user);

            return pair;
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
