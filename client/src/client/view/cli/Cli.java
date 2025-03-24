package client.view.cli;

import java.io.Console;
import java.io.IOError;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.View;
import client.view.inputrequests.InputRequest;
import client.view.inputrequests.InputRequestFactory;

/**
 * Консольное пользовательское представление.
 */
public class Cli extends View {
    private final Console console = System.console();

    @Override
    public void output(String msg) {
        console.printf(msg + "\n");
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    @Override
    public String input(String msg) throws InputDeniedException, UnexpectedEODException {
        try {
            console.flush();
            
            String inputString = console.readLine(msg);
            if (inputString == null) {
                throw new UnexpectedEODException();
            }

            return inputString.trim();
        } catch (IOError e) {
            throw new InputDeniedException(e.getMessage());
        }
    }

    @Override
    public <T> T input(InputRequest<T> req)
        throws InputDeniedException, InputFailedException, UnexpectedEODException
    {
        return req.request();
    }

    @Override
    public InputRequestFactory getInputRequestFactory() {
        return new client.view.cli.InputRequestFactory(this);
    }
}