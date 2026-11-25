package server.service;

import server.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.UserRepository;
import server.resource.domain.user.User;

import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository userRepository;

    public UserService() {

    }

    @Transactional
    public List<User> load() {
        return userRepository.load();
    }

    @Transactional
    public User find(int id) throws UserNotFoundException {
        return userRepository.find(id);
    }

    @Transactional
    public User findByName(String name) throws UserNotFoundException {
        return userRepository.findByName(name);
    }
}
