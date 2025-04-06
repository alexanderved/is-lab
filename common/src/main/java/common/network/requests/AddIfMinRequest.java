package common.network.requests;

import common.domain.OrganizationTemplate;
import common.utils.CommandInfo;

public class AddIfMinRequest extends NetworkRequest {
    private final OrganizationTemplate template;

    public AddIfMinRequest(OrganizationTemplate template) {
        super(CommandInfo.ADD_IF_MIN.getName());

        this.template = template;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }

    @Override
    public boolean isValid() {
        return template.isValid();
    }
}
