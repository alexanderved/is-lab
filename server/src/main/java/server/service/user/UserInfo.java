package server.service.user;

public record UserInfo(int id, String username, String email,
                       String firstName, String secondName, Role role) {
    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getSecondName(), user.getRole());
    }
}
