package common.network.requests;

import common.domain.OrganizationTemplate;
import common.utils.CommandInfo;

public class RemoveLowerRequest extends NetworkRequest {
    private final OrganizationTemplate template;

    public RemoveLowerRequest(OrganizationTemplate template) {
        super(CommandInfo.REMOVE_LOWER.getName());

        this.template = template;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }
}
