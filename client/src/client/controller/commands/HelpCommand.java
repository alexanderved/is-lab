package client.controller.commands;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.view.View;
import common.utils.CommandInfo;

/**
 * Команда 'help', выводящая справку по доступным командам.
 */
public final class HelpCommand extends Command {
    private static final String CMD_NAME = CommandInfo.HELP.getName();
    private static final int NB_ARGUMENTS = CommandInfo.HELP.getArguments().length;

    private HelpCommand(Runner runner) {
        super(runner);
    }

    @Override
    public void execute() {
        View view = runner.getView();

        for (CommandInfo cmdInfo : CommandInfo.values()) {
            if (cmdInfo.getName() == null) {
                continue;
            }

            String s = cmdInfo.getName();
            for (String arg : cmdInfo.getArguments()) {
                s += " " + arg;
            }
            s += ": " + cmdInfo.getDescription();

            view.output(s);
        }
    }

    /**
     * Парсер команды 'help'.
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

            return new HelpCommand(runner);
        }
    }
}