package common.utils.validators;

import common.domain.User;

public final class UserValidator {
    public static boolean validate(User user) {
        return validateName(user.getName()) && validatePassword(user.getPassword());
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean validatePassword(String password) {
        return password != null;
    }
}
