package client.controller.commands;

import client.controller.Runner;
import client.exceptions.ServerRequestFailedException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import common.network.requests.NetworkRequest;
import common.network.requests.PrintFieldDescendingAnnualTurnoverRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;

/**
 * Команда 'print_field_descending_annual_turnover', выводящая
 * значения поля annualTurnover всех элементов в порядке убывания.
 */
public final class PrintFieldDescendingAnnualTurnoverCommand extends Command {
    private static final String CMD_NAME = CommandInfo.PRINT_FIELD_DESCENDING_ANNUAL_TURNOVER.getName();
    private static final int NB_ARGUMENTS = CommandInfo.PRINT_FIELD_DESCENDING_ANNUAL_TURNOVER.getArguments().length;

    private PrintFieldDescendingAnnualTurnoverCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() throws ServerRequestFailedException {
        NetworkRequest req = new PrintFieldDescendingAnnualTurnoverRequest();
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /**
     * Парсер команды 'print_field_descending_annual_turnover'.
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

            return new PrintFieldDescendingAnnualTurnoverCommand(runner);
        }
    }
}
