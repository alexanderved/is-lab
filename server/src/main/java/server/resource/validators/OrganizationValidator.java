package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.resource.domain.Address;
import server.resource.domain.organization.Organization;
import server.resource.domain.organization.OrganizationType;

@ApplicationScoped
public class OrganizationValidator extends ResourceValidator<Organization> {
    @Inject
    private AddressValidator addressValidator;

    public OrganizationValidator() {

    }

    @Override
    public boolean isValid(Organization res) {
        return isValidInstance(res)
                && (res.getOfficialAddress() == null || addressValidator.isValid(res.getOfficialAddress()))
                && (res.getPostalAddress() == null || addressValidator.isValid(res.getPostalAddress()));
    }

    public static boolean isValidInstance(Organization org) {
        return org != null
                && validateId(org.getId())
                && validateAnnualTurnover(org.getAnnualTurnover())
                && validateEmployeesCount(org.getEmployeesCount())
                && validateFullName(org.getFullName())
                && validateRating(org.getRating())
                && validateType(org.getType())
                && validateOfficialAddress(org.getOfficialAddress())
                && validatePostalAddress(org.getPostalAddress());
    }

    public static boolean validateId(int id) {
        return id > 0;
    }

    public static boolean validateAnnualTurnover(Long annualTurnover) {
        return annualTurnover != null && annualTurnover > 0;
    }

    public static boolean validateEmployeesCount(Long employeesCount) {
        return employeesCount != null && employeesCount > 0;
    }

    public static boolean validateFullName(String fullName) {
        return fullName == null || !fullName.isEmpty();
    }

    public static boolean validateRating(Float rating) {
        return rating == null || rating > 0;
    }

    public static boolean validateType(OrganizationType type) {
        return type != null;
    }

    public static boolean validateOfficialAddress(Address officialAddress) {
        return officialAddress == null || AddressValidator.isValidInstance(officialAddress);
    }

    public static boolean validatePostalAddress(Address postalAddress) {
        return postalAddress == null || AddressValidator.isValidInstance(postalAddress);
    }
}
