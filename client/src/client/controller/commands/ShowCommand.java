package client.controller.commands;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.network.requests.NetworkRequest;
import common.network.requests.ShowRequest;
import common.utils.CommandInfo;

/**
 * Команда 'show', выводящая в стандартный поток вывода
 * все элементы коллекции в строковом представлении.
 */
public final class ShowCommand extends Command {
    private static final String CMD_NAME = CommandInfo.SHOW.getName();
    private static final int NB_ARGUMENTS = CommandInfo.SHOW.getArguments().length;

    private ShowCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() {
        NetworkRequest req = new ShowRequest();

        throw new UnsupportedOperationException();
    }

    /**
     * Парсер команды 'show'.
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

            return new ShowCommand(runner);
        }
    }
}