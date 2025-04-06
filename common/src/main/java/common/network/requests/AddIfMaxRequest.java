package common.network.requests;

import common.domain.OrganizationTemplate;
import common.utils.CommandInfo;

public class AddIfMaxRequest extends NetworkRequest {
    private final OrganizationTemplate template;

    public AddIfMaxRequest(OrganizationTemplate template) {
        super(CommandInfo.ADD_IF_MAX.getName());

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
