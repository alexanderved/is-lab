package client.controller.commands;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.InfoRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;

/**
 * Команда 'info', выводящая в стандартный поток вывода информацию о коллекции.
 */
public final class InfoCommand extends Command {
    private static final String CMD_NAME = CommandInfo.INFO.getName();
    private static final int NB_ARGUMENTS = CommandInfo.INFO.getArguments().length;

    private InfoCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() throws NetworkCommunicationFailedException {
        NetworkRequest req = new InfoRequest();
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /**
     * Парсер команды 'info'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
        throws
            WrongNumberOfArgumentsException,
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

            return new InfoCommand(runner);
        }
    }
}