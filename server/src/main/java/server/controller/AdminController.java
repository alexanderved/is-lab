package server.controller;

import common.exceptions.UserNotFoundException;
import common.network.responses.SuccessResponse;
import org.springframework.web.bind.annotation.*;
import server.exceptions.RepositoryAccessFailedException;
import server.service.admin.AdminService;
import server.service.auth.AuthService;
import server.service.user.Role;
import server.service.user.User;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/appoint/{id}")
    public SuccessResponse appoint(@PathVariable int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        adminService.changeUserRole(id, (role) ->
                switch (role) {
                    case ROLE_USER -> Role.ROLE_ADMIN;
                    case ROLE_ADMIN -> Role.ROLE_USER;
                    default -> role;
                });

        return new SuccessResponse();
    }

    @PostMapping("/block/{id}")
    public SuccessResponse block(@PathVariable int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        adminService.changeUserRole(id, (role) ->
                switch (role) {
                    case ROLE_BLOCKED -> Role.ROLE_USER;
                    case ROLE_SUPERADMIN -> Role.ROLE_SUPERADMIN;
                    default -> Role.ROLE_BLOCKED;
                });

        return new SuccessResponse();
    }
}
