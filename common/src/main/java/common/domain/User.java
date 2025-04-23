package common.domain;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.UserValidator;

import java.io.Serializable;

public class User implements Serializable, Validatable {
    private final String name;
    private final String password;

    public User(String name, String password) throws InvalidFieldFormatException {
        if (!UserValidator.validateName(name)) {
            throw new InvalidFieldFormatException("name");
        }
        this.name = name;

        if (!UserValidator.validatePassword(password)) {
            throw new InvalidFieldFormatException("password");
        }
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean isValid() {
        return UserValidator.validate(this);
    }
}
