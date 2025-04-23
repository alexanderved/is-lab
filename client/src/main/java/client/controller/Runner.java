package client.controller;

import client.controller.commands.Command;
import client.controller.commands.CommandParser;
import client.controller.user.UnauthorizedUser;
import client.exceptions.AuthFailedException;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.network.ServerConnection;
import client.view.View;
import client.view.inputrequests.CommandInputRequest;
import client.view.inputrequests.InputRequestFactory;
import common.domain.User;
import common.exceptions.InvalidFieldFormatException;
import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;

/**
 * Исполнитель пользовательских команд.
 */
public class Runner {
    private State state = State.STOPPED;
    private final View view;
    private final ServerConnection serverConnection;
    private final CommandParser cmdParser;
    private User user;

    public Runner(View view, ServerConnection serverConnection, CommandParser cmdParser) {
        this(view, serverConnection, cmdParser, null);
    }

    public Runner(View view, ServerConnection serverConnection, CommandParser cmdParser, User user) {
        if (view == null) {
            throw new IllegalArgumentException(
                    "`view` в классе `Runner` не должно быть null");
        }
        this.view = view;

        if (serverConnection == null) {
            throw new IllegalArgumentException(
                    "`serverConnection` в классе `Runner` не должно быть null");
        }
        this.serverConnection = serverConnection;

        if (cmdParser == null) {
            throw new IllegalArgumentException(
                    "`cmdParser` в классе `Runner` не должно быть null");
        }
        this.cmdParser = cmdParser;
        this.user = user;
    }

    /**
     * @return представление, из которого поступают команды
     * @see View
     */
    public View getView() {
        return view;
    }

    /**
     * @return соединение с сервером, выполняющим команды
     * @see ServerConnection
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * @return парсер команд пользователя
     */
    public CommandParser getCommandParser() {
        return cmdParser;
    }

    /**
     * @return пользователя клиента или {@code null}, если пользователь не аутентифицирован
     */
    public User getUser() {
        return user;
    }

    /**
     * Цикл исполнения программы.
     */
    public void run() {
        setState(State.RUNNING);

        InputRequestFactory inReqFactory = view.getInputRequestFactory();
        while (getState() == State.RUNNING) {
            if (user == null) {
                try {
                    UnauthorizedUser unauthUser = view.auth();
                    user = unauthUser.auth(this);
                } catch (InvalidFieldFormatException | InputDeniedException
                         | InputFailedException | UnexpectedEODException
                         | NetworkCommunicationFailedException | AuthFailedException e) {
                    view.error("Не удалось произвести аутентификацию пользователя: " + e.getMessage());
                    stop();

                    return;
                } catch (UserNotFoundException | UserNameAlreadyTakenException e) {
                    view.error(e.getMessage());

                    continue;
                }
            }

            try {
                CommandInputRequest cmdInReq = inReqFactory.createCommandInputRequest(this);
                Command cmd = view.input(cmdInReq);
                
                cmd.execute();
            } catch (InputFailedException e) {
                view.error(e.getMessage());
            } catch (InputDeniedException e) {
                view.error("Не удалось прочитать команду: " + e.getMessage());

                stop();
            } catch (NetworkCommunicationFailedException e) {
                view.error("Не удалось связаться с сервером: " + e.getMessage());

                stop();
            } catch (UnexpectedEODException e) {
                stop();
            }
        }
    }

    /**
     * Останавливает выполнение программы.
     */
    public void stop() {
        setState(State.STOPPED);
    }

    private State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }

    private enum State {
        STOPPED, RUNNING;
    }
}
