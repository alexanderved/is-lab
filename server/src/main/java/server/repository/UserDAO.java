package server.repository;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import server.exceptions.RepositoryAccessFailedException;
import server.service.auth.HashedUser;

public abstract class UserDAO {
    public abstract HashedUser findByName(String name)
            throws RepositoryAccessFailedException, UserNotFoundException;
    public abstract void add(HashedUser user)
            throws RepositoryAccessFailedException, UserNameAlreadyTakenException;
    public abstract void update(HashedUser user)
            throws RepositoryAccessFailedException, UserNotFoundException;
}
