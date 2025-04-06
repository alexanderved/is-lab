package client.view.cli;

import client.controller.Runner;
import client.controller.commands.Command;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;

class CommandInputRequest
        extends client.view.inputrequests.CommandInputRequest {
    private Cli cli;

    CommandInputRequest(Cli cli, Runner runner) {
        super(runner);

        this.cli = cli;
    }

    @Override
    public Cli getView() {
        return cli;
    }

    @Override
    public Command request()
            throws InputDeniedException, InputFailedException, UnexpectedEODException {
        try {
            return runner.getCommandParser().parse(cli.input("> "), runner);
        } catch (WrongNumberOfArgumentsException | InvalidArgumentFormatException
                | UnsupportedCommandException e) {
            throw new InputFailedException(e.getMessage());
        }
    }
}
