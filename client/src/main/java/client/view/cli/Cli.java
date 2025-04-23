package client.view.cli;

import java.io.Console;
import java.io.IOError;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.View;
import client.view.inputrequests.InputRequest;
import client.view.inputrequests.InputRequestFactory;
import common.exceptions.InvalidFieldFormatException;
import common.utils.validators.UserValidator;
import client.controller.user.LoggedOutUser;
import client.controller.user.UnauthorizedUser;
import client.controller.user.UnregisteredUser;

/**
 * Консольное пользовательское представление.
 */
public class Cli extends View {
    private final Console console = System.console();

    @Override
    public UnauthorizedUser auth()
            throws InvalidFieldFormatException, InputDeniedException, InputFailedException, UnexpectedEODException {
        String doRegistration = input("Желаете зарегистрировать нового пользователя (y/N)? ");

        while (true) {
            String username = input("Введите имя пользователя: ");
            if (!UserValidator.validateName(username)) {
                output("Имя пользователя не валидно");
                continue;

            }

            String password = input("Введите пароль: ");
            if (password == null) {
                password = "";
            }

            if (!UserValidator.validatePassword(password)) {
                output("Пароль пользователя не валиден");
                continue;

            }

            if (doRegistration.equalsIgnoreCase("y")) {
                String passwordConfirmation = input("Введите подтверждение пароля: ");

                if (!password.equals(passwordConfirmation)) {
                    throw new InputFailedException("Пароль и подтверждение пароля должны совпадать");
                }

                return new UnregisteredUser(username, password);
            }

            return new LoggedOutUser(username, password);
        }
    }

    @Override
    public void output(String msg) {
        console.printf(msg + "\n");
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    @Override
    public String input(String msg) throws InputDeniedException, UnexpectedEODException {
        try {
            console.flush();
            
            String inputString = console.readLine(msg);
            if (inputString == null) {
                throw new UnexpectedEODException();
            }

            return inputString.trim();
        } catch (IOError e) {
            throw new InputDeniedException(e.getMessage());
        }
    }

    @Override
    public <T> T input(InputRequest<T> req)
        throws InputDeniedException, InputFailedException, UnexpectedEODException
    {
        return req.request();
    }

    @Override
    public InputRequestFactory getInputRequestFactory() {
        return new client.view.cli.InputRequestFactory(this);
    }
}