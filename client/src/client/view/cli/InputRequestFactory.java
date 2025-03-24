package client.view.cli;

import client.controller.Runner;
import client.view.inputrequests.CompositeDataInputRequest;
import client.view.inputrequests.DataInputRequest;
import client.view.inputrequests.PrimitiveDataInputRequest;
import client.view.inputrequests.CompositeDataInputRequest.CompositeDataParser;
import client.view.inputrequests.PrimitiveDataInputRequest.PrimitiveDataParser;

class InputRequestFactory extends client.view.inputrequests.InputRequestFactory {
    private Cli cli;

    InputRequestFactory(Cli cli) {
        this.cli = cli;
    }

    @Override
    public <T> PrimitiveDataInputRequest<T> createPrimitiveDataInputRequest(
            String requestText, String commentText, PrimitiveDataParser<T> parser) {
        return new client.view.cli.PrimitiveDataInputRequest<>(cli, requestText, commentText, parser);
    }

    @Override
    public <E, T> CompositeDataInputRequest<E, T> createCompositeDataInputRequest(
            String requestText, String commentText, CompositeDataParser<E, T> parser) {
        return new client.view.cli.CompositeDataInputRequest<>(cli, requestText, commentText, parser);
    }

    public <T> OptionalDataInputRequest<T> createOptionalInputRequest(DataInputRequest<T> req) {
        return new client.view.cli.OptionalDataInputRequest<>(cli, req);
    }

    @Override
    public CommandInputRequest createCommandInputRequest(Runner runner) {
        return new client.view.cli.CommandInputRequest(cli, runner);
    }
}
