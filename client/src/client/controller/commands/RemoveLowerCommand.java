package client.controller.commands;

import client.controller.Runner;
import common.domain.OrganizationTemplate;
import common.network.requests.NetworkRequest;
import common.network.requests.RemoveLowerRequest;
import common.utils.CommandInfo;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.utils.OrganizationTemplateCollector;

/**
 * Команда 'remove_lower', удаляющая из коллекции все элементы, меньшие, чем заданный.
 */
public class RemoveLowerCommand extends Command {
    private static final String CMD_NAME = CommandInfo.REMOVE_LOWER.getName();
    private static final int NB_ARGUMENTS = CommandInfo.REMOVE_LOWER.getArguments().length;

    private final OrganizationTemplate template;

    private RemoveLowerCommand(OrganizationTemplate template, Runner runner) {
        super(runner);

        if (template == null) {
            throw new IllegalArgumentException("Поле `template` не может быть null");
        }

        this.template = template;
    }

    @Override
    public void execute() {
        NetworkRequest req = new RemoveLowerRequest(template);

        throw new UnsupportedOperationException();
    }
    
    /**
     * Парсер команды 'remove_lower'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
        throws
            WrongNumberOfArgumentsException,
            UnsupportedCommandException,
            InputDeniedException,
            InputFailedException,
            UnexpectedEODException
        {
            String[] args = cmd.split(" ");
            if (!args[0].equals(CMD_NAME)) {
                throw new UnsupportedCommandException(args[0]);
            }

            if (args.length - 1 != NB_ARGUMENTS) {
                throw new WrongNumberOfArgumentsException(args[0],
                        args.length - 1, NB_ARGUMENTS);
            }

            OrganizationTemplate template = OrganizationTemplateCollector.collect(runner.getView());

            return new RemoveLowerCommand(template, runner);
        }
    }
}
