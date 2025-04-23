package common.network.requests;

import common.domain.OrganizationTemplate;
import common.domain.User;
import common.utils.CommandInfo;

public class UpdateRequest extends NetworkRequest {
    private final User user;
    private final int id;
    private final OrganizationTemplate template;

    public UpdateRequest(User user, int id, OrganizationTemplate template) {
        super(CommandInfo.UPDATE.getName());

        this.user = user;
        this.id = id;
        this.template = template;
    }

    public User getUser() {
        return user;
    }

    public int getID() {
        return id;
    }

    public OrganizationTemplate getOrganizationTemplate() {
        return template;
    }

    @Override
    public boolean isValid() {
        return template.isValid() && user.isValid();
    }
}
