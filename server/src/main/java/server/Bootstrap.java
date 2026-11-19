package server;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.requests.RegistrationRequest;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.AUserRepository;
import server.service.auth.AuthService;

@Singleton
@Startup
public class Bootstrap {
    @Inject
    private AUserRepository userRepository;

    @Inject
    private AuthService authService;

    @PostConstruct
    public void init() {
        try {
            userRepository.find(0);
        } catch (RepositoryAccessFailedException ignored) {

        } catch (UserNotFoundException e) {
            try {
                authService.registerSuperadmin(
                        new RegistrationRequest("root", "root"));
            } catch (UserNameAlreadyTakenException | UserDBAccessFailedException ignored) {

            }
        }
    }
}