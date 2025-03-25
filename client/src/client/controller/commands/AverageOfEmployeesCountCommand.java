package client.controller.commands;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.network.requests.NetworkRequest;
import common.utils.CommandInfo;
import common.network.requests.AverageOfEmployeesCountRequest;

/**
 * Команда 'average_of_employees_count', выводящая среднее значение поля employeesCount для всех элементов коллекции.
 */
public final class AverageOfEmployeesCountCommand extends Command {
    private static final String CMD_NAME = CommandInfo.AVERAGE_OF_EMPLOYEES_COUNT.getName();
    private static final int NB_ARGUMENTS = CommandInfo.AVERAGE_OF_EMPLOYEES_COUNT.getArguments().length;

    private AverageOfEmployeesCountCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() {
        NetworkRequest req = new AverageOfEmployeesCountRequest();

        throw new UnsupportedOperationException();
    }

    /**
     * Парсер команды 'average_of_employees_count'.
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

            return new AverageOfEmployeesCountCommand(runner);
        }
    }
}
