package server.service.auth;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import server.exceptions.InvalidRefreshTokenException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.UserDAO;
import server.service.token.Token;
import server.service.token.TokenPair;
import server.service.token.TokenService;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Service
public class AuthService {
    private static final Random RANDOM = new SecureRandom();

    private final UserDAO userDAO;
    private final Hasher hasher;
    public final TokenService tokenService;

    public AuthService(UserDAO userDAO, Hasher hasher, TokenService tokenService) {
        this.userDAO = userDAO;
        this.hasher = hasher;
        this.tokenService = tokenService;
    }

    public HashedUser findByName(String name)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return userDAO.findByName(name);
    }

    public void register(User user)
            throws UserNameAlreadyTakenException, UserDBAccessFailedException {
        String salt = generateSalt();
        String passwordHash = hashPassword(user.getPassword(), salt);

        try {
            userDAO.add(new HashedUser(user.getUsername(), user.getRole(), passwordHash, salt));
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    public TokenPair login(User user)
            throws UserNotFoundException, UserDBAccessFailedException {
        try {
            HashedUser hashedUser = findByName(user.getUsername());
            String hashedPassword = hashPassword(user.getPassword(), hashedUser.getSalt());
            if (!hashedPassword.equals(hashedUser.getPasswordHash())) {
                throw new UserNotFoundException();
            }

            TokenPair pair = tokenService.generateTokenPair(hashedUser);
            hashedUser.addRefreshToken(tokenService.generateTokenString(pair.getRefresh()));
            userDAO.update(hashedUser);

            return pair;
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    public TokenPair refresh(Token token)
            throws UserDBAccessFailedException, InvalidRefreshTokenException {
        try {
            HashedUser hashedUser = userDAO.findByName(token.getUsername());
            if (!tokenService.isRefreshTokenValid(token, hashedUser)) {
                throw new InvalidRefreshTokenException();
            }

            TokenPair pair = tokenService.generateTokenPair(hashedUser);

            hashedUser.removeRefreshToken(tokenService.generateTokenString(token));
            hashedUser.addRefreshToken(tokenService.generateTokenString(pair.getRefresh()));
            userDAO.update(hashedUser);

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
