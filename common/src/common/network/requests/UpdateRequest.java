package common.network.requests;

import common.domain.OrganizationTemplate;
import common.utils.CommandInfo;

public class UpdateRequest extends NetworkRequest {
    private final int id;
    private final OrganizationTemplate template;

    public UpdateRequest(int id, OrganizationTemplate template) {
        super(CommandInfo.UPDATE.getName());

        this.id = id;
        this.template = template;
    }

    public int getID() {
        return id;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }
}
