package client.controller.commands;

import client.controller.Runner;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.requests.RemoveByIDRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;

/**
 * Команда 'remove_by_id', удаляющая элемент из коллекции по его id.
 */
public class RemoveByIDCommand extends Command {
    private static final String CMD_NAME = CommandInfo.REMOVE_BY_ID.getName();
    private static final int NB_ARGUMENTS = CommandInfo.REMOVE_BY_ID.getArguments().length;

    private final int id;

    private RemoveByIDCommand(int id, Runner runner) {
        super(runner);

        this.id = id;
    }
    
    @Override
    public void execute() throws NetworkCommunicationFailedException {
        NetworkRequest req = new RemoveByIDRequest(id);
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /** 
     * Парсер команды 'remove_by_id'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
        throws
            WrongNumberOfArgumentsException,
            InvalidArgumentFormatException,
            UnsupportedCommandException
        {
            String[] args = cmd.split(" ");
            if (!args[0].equals(CMD_NAME)) {
                throw new UnsupportedCommandException(args[0]);
            }

            if (args.length - 1 != NB_ARGUMENTS) {
                throw new WrongNumberOfArgumentsException(args[0],
                        args.length - 1, NB_ARGUMENTS);
            }

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException | NullPointerException e) {
                throw new InvalidArgumentFormatException(CMD_NAME, "id", "Целое число");
            }

            return new RemoveByIDCommand(id, runner);
        }
    }
}
