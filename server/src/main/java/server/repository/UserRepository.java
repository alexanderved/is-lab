package server.repository;

import server.exceptions.UserNameAlreadyTakenException;
import server.exceptions.UserNotFoundException;
import server.exceptions.RepositoryAccessFailedException;
import server.resource.domain.user.User;
import server.resource.domain.user.UserTemplate;

import java.util.List;

public abstract class UserRepository {
    public abstract List<User> load();
    public abstract User find(int id) throws UserNotFoundException;
    public abstract User findByName(String name) throws UserNotFoundException;
    public abstract void createSuperadmin(UserTemplate template) throws UserNameAlreadyTakenException;
    public abstract void add(UserTemplate template) throws UserNameAlreadyTakenException;
    public abstract void update(User user) throws UserNotFoundException;
}
