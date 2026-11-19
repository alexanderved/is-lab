package server.service.user;

public record UserInfo(int id, String username, Role role) {
    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getId(), user.getUsername(), user.getRole());
    }
}
