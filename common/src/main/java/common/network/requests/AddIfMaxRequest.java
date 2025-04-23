package common.network.requests;

import common.domain.OrganizationTemplate;
import common.domain.User;
import common.utils.CommandInfo;

public class AddIfMaxRequest extends NetworkRequest {
    private final User user;
    private final OrganizationTemplate template;

    public AddIfMaxRequest(User user, OrganizationTemplate template) {
        super(CommandInfo.ADD_IF_MAX.getName());

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
