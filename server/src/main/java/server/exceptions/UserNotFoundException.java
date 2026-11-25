package server.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("Пользователь не найден");
    }

    public UserNotFoundException(String username) {
        super("Пользователь '" + username + "' не найден");
    }
}
