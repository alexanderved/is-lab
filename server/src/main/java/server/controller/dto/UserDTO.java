package server.controller.dto;

import server.service.auth.Role;
import server.service.auth.User;
import common.exceptions.InvalidFieldFormatException;

public record UserDTO(String name, Role role, String password) {
    public static UserDTO fromDomain(User user) {
        return new UserDTO(user.getUsername(), user.getRole(), user.getPassword());
    }

    public User toDomain() throws InvalidFieldFormatException {
        return new User(name, role, password);
    }
}
