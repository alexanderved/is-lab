package client.controller.commands;

import client.controller.Runner;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.FilterStartsWithFullNameRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;

/**
 * Команда 'filter_starts_with_full_name', выводящая элементы, значение поля
 * fullName которых начинается с заданной подстроки.
 */
public class FilterStartsWithFullNameCommand extends Command {
    private static final String CMD_NAME = CommandInfo.FILTER_STARTS_WITH_FULL_NAME.getName();
    private static final int NB_ARGUMENTS = CommandInfo.FILTER_STARTS_WITH_FULL_NAME.getArguments().length;

    private final String fullName;

    private FilterStartsWithFullNameCommand(String fullName, Runner runner) {
        super(runner);

        this.fullName = fullName;
    }
    
    @Override
    public void execute() throws NetworkCommunicationFailedException {
        NetworkRequest req = new FilterStartsWithFullNameRequest(fullName);
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /**
     * Парсер команды 'filter_starts_with_full_name'.
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
                throw new WrongNumberOfArgumentsException(
                    args[0], args.length - 1, NB_ARGUMENTS);
            }

            return new FilterStartsWithFullNameCommand(args[1], runner);
        }
    }
}
