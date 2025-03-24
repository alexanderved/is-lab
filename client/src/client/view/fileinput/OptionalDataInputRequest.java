package client.view.fileinput;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.inputrequests.DataInputRequest;

class OptionalDataInputRequest<T> extends client.view.inputrequests.OptionalDataInputRequest<T> {
    private FileInputView fileInputView;

    OptionalDataInputRequest(FileInputView fileInputView, DataInputRequest<T> req) {
        super(req);

        this.fileInputView = fileInputView;
    }

    @Override
    public FileInputView getView() {
        return fileInputView;
    }

    @Override
    public T request() throws InputDeniedException, InputFailedException, UnexpectedEODException {
        return fileInputView.input().equals("y") ? fileInputView.input(req) : null;
    }    
}
