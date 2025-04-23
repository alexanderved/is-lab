package server.application;

import common.domain.User;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import server.repository.HashedUser;
import server.repository.Repository;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class UserManager {
    private static final Random RANDOM = new SecureRandom();

    private final Hasher hasher;
    private final Repository repo;

    public UserManager(Hasher hasher, Repository repo) {
        if (hasher == null) {
            throw new IllegalArgumentException(
                    "`hasher` не должно быть null");
        }
        this.hasher = hasher;

        if (repo == null) {
            throw new IllegalArgumentException(
                    "`repo` не должно быть null");
        }
        this.repo = repo;
    }

    public int getUserID(User user) throws UserNotFoundException, UserDBAccessFailedException {
        try {
            HashedUser hashedUser = repo.getHashedUser(user.getName());

            if (hasher.hash(user.getPassword() + hashedUser.getSalt()).equals(hashedUser.getPasswordHash())) {
                return hashedUser.getId();
            } else {
                throw new UserNotFoundException();
            }
        } catch (RepositoryAccessFailedException e) {
            throw new UserDBAccessFailedException(e);
        }
    }

    public void registerUser(User user) throws UserDBAccessFailedException, UserNameAlreadyTakenException {
        try {
            getUserID(user);

            throw new UserNameAlreadyTakenException(user.getName());
        } catch (UserNotFoundException ignore) {
            byte[] saltBytes = new byte[16];
            RANDOM.nextBytes(saltBytes);

            String salt = Base64.getEncoder().encodeToString(saltBytes);
            String passwordHash = hasher.hash(user.getPassword() + salt);

            try {
                repo.registerUser(user.getName(), passwordHash, salt);
            } catch (RepositoryAccessFailedException e) {
                throw new UserDBAccessFailedException(e);
            }
        }
    }

    public void loginUser(User user) throws UserNotFoundException, UserDBAccessFailedException {
        getUserID(user);
    }
}
