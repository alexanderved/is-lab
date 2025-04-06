package client.view.fileinput;

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
    private FileInputView fileInputView;

    CommandInputRequest(FileInputView fileInputView, Runner runner) {
        super(runner);

        this.fileInputView = fileInputView;
    }

    @Override
    public FileInputView getView() {
        return fileInputView;
    }

    @Override
    public Command request()
            throws InputDeniedException, InputFailedException, UnexpectedEODException {
        try {
            return runner.getCommandParser().parse(fileInputView.input(), runner);
        } catch (WrongNumberOfArgumentsException | InvalidArgumentFormatException
                | UnsupportedCommandException e) {
            throw new InputFailedException(e.getMessage());
        }
    }
}
