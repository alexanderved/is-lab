package server.service;

import server.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.UserRepository;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;

import java.util.function.UnaryOperator;

@ApplicationScoped
public class AdminService {
    @Inject
    private UserRepository userRepository;

    public AdminService() {

    }

    @Transactional
    public void changeUserRole(int id, UnaryOperator<Role> changer)
            throws UserNotFoundException, RepositoryAccessFailedException {
        User user = userRepository.find(id);
        user.setRole(changer.apply(user.getRole()));
        userRepository.update(user);
    }
}
