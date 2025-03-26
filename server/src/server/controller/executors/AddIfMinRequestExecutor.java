package server.controller.executors;

import common.domain.OrganizationTemplate;
import common.network.requests.AddIfMinRequest;
import common.network.requests.NetworkRequest;
import common.network.responses.AddResponse;
import common.network.responses.FailureResponse;
import common.network.responses.NetworkResponse;
import common.utils.CommandInfo;
import server.application.CollectionManager;
import server.controller.Runner;
import server.exceptions.UnsupportedRequestException;

public class AddIfMinRequestExecutor extends RequestExecutor {
    @Override
    public NetworkResponse execute(NetworkRequest req, Runner runner) throws UnsupportedRequestException {
        if (!req.getName().equals(CommandInfo.ADD_IF_MIN.getName()) || !(req instanceof AddIfMinRequest)) {
            throw new UnsupportedRequestException(req);
        }

        CollectionManager manager = runner.getCollectionManager();

        AddIfMinRequest addReq = (AddIfMinRequest) req;
        OrganizationTemplate template = addReq.getOrganizationTemplate();
        if (manager.min() != null && template.compareTo(manager.min()) >= 0) {
            return new FailureResponse("Новая организация не добавлена в коллекцию, так как"
                    + " ее значение не меньше значения наименьшего элемента коллекции");
        }

        return new AddResponse(runner.getCollectionManager().add(template));
    }
}
