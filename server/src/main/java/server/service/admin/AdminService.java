package server.service.admin;

import common.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.AUserRepository;
import server.service.user.Role;
import server.service.user.User;

import java.util.function.UnaryOperator;

@ApplicationScoped
public class AdminService {
    @Inject
    private AUserRepository AUserRepository;

    public AdminService() {

    }

    @Transactional
    public void changeUserRole(int id, UnaryOperator<Role> changer)
            throws UserNotFoundException, RepositoryAccessFailedException {
        User user = AUserRepository.find(id);
        user.setRole(changer.apply(user.getRole()));
        AUserRepository.update(user);
    }
}
