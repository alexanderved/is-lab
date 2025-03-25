package client.controller.commands;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.network.requests.NetworkRequest;
import common.utils.CommandInfo;
import common.network.requests.ClearRequest;

/**
 * Команда 'clear', очищающая коллекцию.
 */
public final class ClearCommand extends Command {
    private static final String CMD_NAME = CommandInfo.CLEAR.getName();
    private static final int NB_ARGUMENTS = CommandInfo.CLEAR.getArguments().length;

    private ClearCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() {
        NetworkRequest req = new ClearRequest();

        throw new UnsupportedOperationException();
    }

    /**
     * Парсер команды 'clear'.
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

            return new ClearCommand(runner);
        }
    }
}