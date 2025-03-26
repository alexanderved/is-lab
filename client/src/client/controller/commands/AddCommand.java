package client.controller.commands;

import client.controller.Runner;
import common.domain.OrganizationTemplate;
import common.network.requests.AddRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.ErrorResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.ServerRequestFailedException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.utils.OrganizationTemplateCollector;

/**
 * Команда 'add', добавляющая новый элемент в коллекцию.
 */
public class AddCommand extends Command {
    private static final String CMD_NAME = CommandInfo.ADD.getName();
    private static final int NB_ARGUMENTS = CommandInfo.ADD.getArguments().length - 1;

    private final OrganizationTemplate template;

    private AddCommand(OrganizationTemplate template, Runner runner) {
        super(runner);

        if (template == null) {
            throw new IllegalArgumentException("Поле `template` не может быть null");
        }

        this.template = template;
    }

    @Override
    public void execute() throws ServerRequestFailedException {
        NetworkRequest req = new AddRequest(template);
        NetworkResponse res = runner.getServerConnection().request(req);

        if (res instanceof ErrorResponse) {
            runner.getView().error(res.getMessage());
        } else {
            runner.getView().output(res.getMessage());
        }
    }

    /**
     * Парсер команды 'add'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
                throws WrongNumberOfArgumentsException, UnsupportedCommandException, InputDeniedException,
                InputFailedException, UnexpectedEODException {
            String[] args = cmd.split(" ");
            if (!args[0].equals(CMD_NAME)) {
                throw new UnsupportedCommandException(args[0]);
            }

            if (args.length - 1 != NB_ARGUMENTS) {
                throw new WrongNumberOfArgumentsException(args[0],
                        args.length - 1, NB_ARGUMENTS);
            }

            OrganizationTemplate template = OrganizationTemplateCollector.collect(runner.getView());

            return new AddCommand(template, runner);
        }
    }
}
