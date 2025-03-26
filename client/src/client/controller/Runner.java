package client.controller;

import client.controller.commands.Command;
import client.controller.commands.CommandParser;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.ServerRequestFailedException;
import client.exceptions.UnexpectedEODException;
import client.network.ServerConnection;
import client.view.View;
import client.view.inputrequests.CommandInputRequest;
import client.view.inputrequests.InputRequestFactory;

public class Runner {
    private State state = State.STOPPED;
    private View view;
    private ServerConnection serverConnection;
    private CommandParser cmdParser;

    public Runner(View view, ServerConnection serverConnection, CommandParser cmdParser) {
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

    }

    /**
     * @return представление, из которого поступают команды
     * @see View
     */
    public View getView() {
        return view;
    }

    /**
     * Устанавливает представление, из которого поступают команды.
     * @param view представление, из которого поступают команды
     * @see View
     */
    public void setView(View view) {
        if (view == null) {
            throw new IllegalArgumentException(
                    "`view` в классе `Runner` не должно быть null");
        }

        this.view = view;
    }

    /**
     * @return соединение с сервером, выполняющим команды
     * @see ServerConnection
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Устанавливает соединение с сервером, выполняющим команды.
     * @param serverConnection Соединение с сервером, выполняющим команды
     */
    public void setServerConnection(ServerConnection serverConnection) {
        if (serverConnection == null) {
            throw new IllegalArgumentException(
                    "`serverConnection` в классе `Runner` не должно быть null");
        }
        this.serverConnection = serverConnection;
    }

    /**
     * @return парсер команд пользователя
     */
    public CommandParser getCommandParser() {
        return cmdParser;
    }

    /**
     * Устанавливает парсер команд пользователя.
     * @param cmdParser Парсер команд
     */
    public void setCommandParser(CommandParser cmdParser) {
        if (cmdParser == null) {
            throw new IllegalArgumentException(
                    "`cmdParser` в классе `Runner` не должно быть null");
        }
        this.cmdParser = cmdParser;
    }

    /**
     * Цикл исполнения программы.
     */
    public void run() {
        setState(State.RUNNING);

        InputRequestFactory inReqFactory = view.getInputRequestFactory();
        while (getState() == State.RUNNING) {
            try {
                CommandInputRequest cmdInReq = inReqFactory.createCommandInputRequest(this);
                Command cmd = view.input(cmdInReq);
                
                cmd.execute();
            } catch (InputFailedException e) {
                view.error(e.getMessage());
            } catch (InputDeniedException e) {
                view.error("Не удалось прочитать команду: " + e.getMessage());

                stop();
            } catch (ServerRequestFailedException e) {
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
