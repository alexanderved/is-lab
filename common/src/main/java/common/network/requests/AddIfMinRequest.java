package common.network.requests;

import common.domain.OrganizationTemplate;
import common.domain.User;
import common.utils.CommandInfo;

public class AddIfMinRequest extends NetworkRequest {
    private final User user;
    private final OrganizationTemplate template;

    public AddIfMinRequest(User user, OrganizationTemplate template) {
        super(CommandInfo.ADD_IF_MIN.getName());

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
