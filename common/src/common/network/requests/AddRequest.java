package common.network.requests;

import common.domain.OrganizationTemplate;
import common.utils.CommandInfo;

public class AddRequest extends NetworkRequest {
    private final OrganizationTemplate template;

    public AddRequest(OrganizationTemplate template) {
        super(CommandInfo.ADD.getName());

        this.template = template;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }
}
