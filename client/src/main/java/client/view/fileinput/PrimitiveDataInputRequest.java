package client.view.fileinput;

import common.exceptions.DataParserException;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;

final class PrimitiveDataInputRequest<T>
        extends client.view.inputrequests.PrimitiveDataInputRequest<T> {
    private FileInputView fileInputView;

    PrimitiveDataInputRequest(
            FileInputView fileInputView, String requestText, PrimitiveDataParser<T> parser) {
        this(fileInputView, requestText, null, parser);
    }

    PrimitiveDataInputRequest(
            FileInputView fileInputView, String requestText, String commentText, PrimitiveDataParser<T> parser) {
        super(requestText, commentText, parser);

        this.fileInputView = fileInputView;
    }

    @Override
    public FileInputView getView() {
        return fileInputView;
    }

    @Override
    public T request() throws InputDeniedException, InputFailedException, UnexpectedEODException {
        try {
            String fieldString = fileInputView.input();
            fieldString = fieldString.isEmpty() ? null : fieldString;

            return parser.parse(fieldString);
        } catch (DataParserException e) {
            throw new InputFailedException(e);
        }
    }
}
