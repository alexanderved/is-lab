package server;

import server.exceptions.UserNameAlreadyTakenException;
import server.exceptions.UserNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.UserRepository;
import server.resource.dto.UserCredentials;
import server.service.auth.AuthService;

@Singleton
@Startup
public class Bootstrap {
    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthService authService;

    @PostConstruct
    public void init() {
        try {
            userRepository.find(0);
        } catch (UserNotFoundException e) {
            try {
                authService.registerSuperadmin(
                        new UserCredentials("root", "root"));
            } catch (UserNameAlreadyTakenException | UserDBAccessFailedException ignored) {

            }
        }
    }
}