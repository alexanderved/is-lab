package client.controller.commands;

import client.controller.Runner;
import common.domain.OrganizationTemplate;
import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import common.network.requests.AddIfMinRequest;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.utils.OrganizationTemplateCollector;

/**
 * Команда 'add_if_min', добавляющая новый элемент в коллекцию,
 * если его значение меньше, чем у наименьшего элемента этой коллекции.
 */
public class AddIfMinCommand extends Command {
    private static final String CMD_NAME = CommandInfo.ADD_IF_MIN.getName();
    private static final int NB_ARGUMENTS = CommandInfo.ADD_IF_MIN.getArguments().length - 1;

    private final OrganizationTemplate template;

    private AddIfMinCommand(OrganizationTemplate template, Runner runner) {
        super(runner);

        if (template == null) {
            throw new IllegalArgumentException("Поле `template` не может быть null");
        }

        this.template = template;
    }

    @Override
    public void execute() throws NetworkCommunicationFailedException {
        NetworkRequest req = new AddIfMinRequest(template);
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }
    
    /**
     * Парсер команды 'add_if_min'.
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

            return new AddIfMinCommand(template, runner);
        }
    }
}
