package server.service.admin;

import common.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.AUserRepository;
import server.service.user.Role;
import server.service.user.User;

import java.util.function.UnaryOperator;

@Service
public class AdminService {
    private final AUserRepository AUserRepository;

    public AdminService(AUserRepository AUserRepository) {
        this.AUserRepository = AUserRepository;
    }

    public void changeUserRole(int id, UnaryOperator<Role> changer)
            throws UserNotFoundException, RepositoryAccessFailedException {
        User user = AUserRepository.find(id);
        user.setRole(changer.apply(user.getRole()));
        AUserRepository.update(user);
    }
}
