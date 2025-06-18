package server.controller;

import common.exceptions.UserNotFoundException;
import common.network.responses.SuccessResponse;
import org.springframework.web.bind.annotation.*;
import server.exceptions.RepositoryAccessFailedException;
import server.service.user.Role;
import server.service.user.User;
import server.service.user.UserInfo;
import server.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
         this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserInfo> list(@RequestParam(defaultValue = "") String filter)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return userService.load()
                .stream()
                .map(UserInfo::fromUser)
                .filter((u) -> u.username().contains(filter))
                .toList();
    }

    @GetMapping("/users/{username}")
    public UserInfo findByName(@PathVariable String username)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return UserInfo.fromUser(userService.findByName(username));
    }
}
