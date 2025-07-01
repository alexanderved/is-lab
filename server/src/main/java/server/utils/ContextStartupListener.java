package server.utils;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.requests.RegistrationRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserDBAccessFailedException;
import server.repository.AUserRepository;
import server.service.admin.AdminService;
import server.service.auth.AuthService;
import server.service.user.Role;
import server.service.user.User;
import server.service.user.UserTemplate;
import server.utils.logger.Logger;

@Component
public class ContextStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private final AUserRepository userRepository;
    private final AuthService authService;

    public ContextStartupListener(
            AUserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!isRootApplicationContext(event)) {
            return;
        }

        try {
            userRepository.find(0);
        } catch (RepositoryAccessFailedException ignored) {

        } catch (UserNotFoundException e) {
            try {
                authService.registerSuperadmin(new RegistrationRequest(
                        "root", "root", null, null, "root"));

                System.out.println("ROOT USER: root");
                System.out.println("ROOT PASSWORD: root");
            } catch (UserNameAlreadyTakenException | UserDBAccessFailedException ignored) {

            }
        }
    }

    private boolean isRootApplicationContext(ContextRefreshedEvent event) {
        return null == event.getApplicationContext().getParent();
    }
}