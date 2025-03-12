package proglab.view.cli;

import proglab.controller.Runner;
import proglab.controller.commands.Command;
import proglab.exceptions.InputDeniedException;
import proglab.exceptions.InputFailedException;
import proglab.exceptions.InvalidArgumentFormatException;
import proglab.exceptions.UnexpectedEODException;
import proglab.exceptions.UnsupportedCommandException;
import proglab.exceptions.WrongNumberOfArgumentsException;

class CommandInputRequest
        extends proglab.view.inputrequests.CommandInputRequest {
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
