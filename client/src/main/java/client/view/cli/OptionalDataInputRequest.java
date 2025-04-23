package client.view.cli;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.inputrequests.DataInputRequest;

class OptionalDataInputRequest<T> extends client.view.inputrequests.OptionalDataInputRequest<T> {
    private Cli cli;

    OptionalDataInputRequest(Cli cli, DataInputRequest<T> req) {
        super(req);

        this.cli = cli;
    }

    @Override
    public Cli getView() {
        return cli;
    }

    @Override
    public T request() throws InputDeniedException, InputFailedException, UnexpectedEODException {
        String ans = cli.input("Желаете указать " + req.getRequestText() + " (y/N)? ");
        
        return ans.equals("y") ? cli.input(req) : null;
    }    
}
