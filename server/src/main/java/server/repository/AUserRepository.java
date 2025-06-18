package server.repository;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import server.exceptions.RepositoryAccessFailedException;
import server.service.user.User;
import server.service.user.UserTemplate;

import java.util.List;

public abstract class AUserRepository {
    public abstract List<User> load();
    public abstract User find(int id)
            throws RepositoryAccessFailedException, UserNotFoundException;
    public abstract User findByName(String name)
            throws RepositoryAccessFailedException, UserNotFoundException;
    public abstract void add(UserTemplate template)
            throws RepositoryAccessFailedException, UserNameAlreadyTakenException;
    public abstract void update(User user)
            throws RepositoryAccessFailedException, UserNotFoundException;
}
