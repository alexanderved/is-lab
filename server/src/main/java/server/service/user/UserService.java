package server.service.user;

import common.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.AUserRepository;

import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    private AUserRepository AUserRepository;

    public UserService() {

    }

    @Transactional
    public List<User> load() {
        return AUserRepository.load();
    }

    @Transactional
    public User find(int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return AUserRepository.find(id);
    }

    @Transactional
    public User findByName(String name)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return AUserRepository.findByName(name);
    }
}
