package server.service.user;

import common.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.AUserRepository;

import java.util.List;

@Service
public class UserService {
    private final AUserRepository AUserRepository;

    public UserService(AUserRepository AUserRepository) {
        this.AUserRepository = AUserRepository;
    }

    public List<User> load() {
        return AUserRepository.load();
    }

    public User find(int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return AUserRepository.find(id);
    }

    public User findByName(String name)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return AUserRepository.findByName(name);
    }
}
