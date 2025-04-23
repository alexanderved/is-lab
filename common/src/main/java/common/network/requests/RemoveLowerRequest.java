package common.network.requests;

import common.domain.OrganizationTemplate;
import common.domain.User;
import common.utils.CommandInfo;

public class RemoveLowerRequest extends NetworkRequest {
    private final User user;
    private final OrganizationTemplate template;

    public RemoveLowerRequest(User user, OrganizationTemplate template) {
        super(CommandInfo.REMOVE_LOWER.getName());

        this.user = user;
        this.template = template;
    }

    public User getUser() {
        return user;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }

    @Override
    public boolean isValid() {
        return template.isValid() && user.isValid();
    }
}
