package client.view.fileinput;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.View;
import client.view.inputrequests.InputRequest;
import client.view.inputrequests.InputRequestFactory;
import common.exceptions.InvalidFieldFormatException;
import client.controller.user.LoggedOutUser;
import client.controller.user.UnauthorizedUser;
import client.controller.user.UnregisteredUser;

/**
 * Представление, в котором данные вводятся из файла.
 */
public class FileInputView extends View implements Closeable {
    private final View view;
    private final BufferedReader reader;

    /**
     * @param fileName Путь до файла, из которого осуществляется ввод данных
     * @param view     Пользовательское представление, используемое для вывода
     * @throws FileNotFoundException если файл, из которого осуществляется ввод
     *                               данных, не найден
     */
    public FileInputView(String fileName, View view) throws FileNotFoundException {
        this.view = view;
        reader = new BufferedReader(new FileReader(fileName));
    }

    @Override
    public UnauthorizedUser auth()
            throws InvalidFieldFormatException, InputDeniedException, InputFailedException, UnexpectedEODException {
        String doRegistration = input();

        String username = input();
        String password = input();

        if (doRegistration.equalsIgnoreCase("y")) {
            String passwordConfirmation = input();

            if (!password.equals(passwordConfirmation)) {
                throw new InputFailedException("Пароль и подтверждение пароля должны совпадать");
            }

            return new UnregisteredUser(username, password);
        }

        return new LoggedOutUser(username, password);
    }

    @Override
    public void output(String msg) {
        view.output(msg);
    }

    @Override
    public void error(String msg) {
        view.error(msg);
    }

    @Override
    public String input(String msg) throws InputDeniedException, UnexpectedEODException {
        String line = null;

        try {
            line = reader.readLine();
        } catch (EOFException e) {
            throw new UnexpectedEODException();
        } catch (IOException e) {
            throw new InputDeniedException(e.getMessage());
        }

        if (line == null) {
            throw new UnexpectedEODException();
        }

        return line;
    }

    @Override
    public <T> T input(InputRequest<T> req)
            throws InputDeniedException, InputFailedException, UnexpectedEODException {
        return req.request();
    }

    @Override
    public InputRequestFactory getInputRequestFactory() {
        return new client.view.fileinput.InputRequestFactory(this);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}