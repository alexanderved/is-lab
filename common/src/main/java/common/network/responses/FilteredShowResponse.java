package common.network.responses;

import java.util.List;

import common.domain.Organization;

public class FilteredShowResponse extends SuccessResponse {
    private final List<Organization> orgs;

    public FilteredShowResponse(List<Organization> orgs) {
        super(null);

        this.orgs = orgs;
    }

    public List<Organization> getOrganizations() {
        return orgs;
    }

    @Override
    public String getMessage() {
        List<String> orgInfo = getOrganizations()
                .stream()
                .map(Organization::toString)
                .toList();

        return "{\n" + String.join(";\n", orgInfo) + "\n}";
    }
}