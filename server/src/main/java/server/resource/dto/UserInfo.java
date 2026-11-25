package server.resource.dto;

import server.resource.domain.user.Role;
import server.resource.domain.user.User;

import java.io.Serializable;

public record UserInfo(int id, String username, Role role) implements Serializable {
    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getId(), user.getUsername(), user.getRole());
    }
}
