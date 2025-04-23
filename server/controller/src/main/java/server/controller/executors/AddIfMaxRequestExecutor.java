package server.controller.executors;

import common.domain.OrganizationTemplate;
import common.network.requests.AddIfMaxRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.AddResponse;
import common.network.responses.ErrorResponse;
import common.network.responses.FailureResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.application.CollectionManager;
import server.controller.Runner;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnsupportedRequestException;

public class AddIfMaxRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.ADD_IF_MAX.getName()) || !(req instanceof AddIfMaxRequest)) {
            throw new UnsupportedRequestException(req);
        }

        CollectionManager manager = runner.getCollectionManager();

        AddIfMaxRequest addReq = (AddIfMaxRequest) req;
        OrganizationTemplate template = addReq.getOrganizationTemplate();
        if (manager.max() != null && template.compareTo(manager.max()) <= 0) {
            return new FailureResponse("Новая организация не добавлена в коллекцию, так как"
                    + " ее значение не превышает значение наибольшего элемента коллекции");
        }

        try {
            return new AddResponse(runner.getCollectionManager().add(addReq.getUser(), template));
        } catch (CollectionOperationFailedException e) {
            return new ErrorResponse("Не удалось добавить организацию");
        }
    }
}
