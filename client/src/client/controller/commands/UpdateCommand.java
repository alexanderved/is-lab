package client.controller.commands;

import client.controller.Runner;
import common.domain.OrganizationTemplate;
import common.network.requests.NetworkRequest;
import common.network.requests.UpdateRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.ServerRequestFailedException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.utils.OrganizationTemplateCollector;

/**
 * Команда 'update', обновляющая значение элемента коллекции, id которого равен заданному.
 */
public class UpdateCommand extends Command {
    private static final String CMD_NAME = CommandInfo.UPDATE.getName();
    private static final int NB_ARGUMENTS = CommandInfo.UPDATE.getArguments().length - 1;

    private final int id;
    private final OrganizationTemplate template;

    private UpdateCommand(int id, OrganizationTemplate template, Runner runner) {
        super(runner);

        if (template == null) {
            throw new IllegalArgumentException("Поле `template` не может быть null");
        }

        this.id = id;
        this.template = template;
    }
    
    @Override
    public void execute() throws ServerRequestFailedException {
        NetworkRequest req = new UpdateRequest(id, template);
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /**
     * Парсер команды 'update'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
        throws
            WrongNumberOfArgumentsException,
            InvalidArgumentFormatException,
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

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException | NullPointerException e) {
                throw new InvalidArgumentFormatException(CMD_NAME, "id", "Целое число");
            }

            OrganizationTemplate template = OrganizationTemplateCollector.collect(runner.getView());

            return new UpdateCommand(id, template, runner);
        }
    }
}
