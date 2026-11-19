package common.utils.validators;

import common.domain.Address;
import common.domain.OrganizationTemplate;
import common.domain.OrganizationType;

public final class OrganizationTemplateValidator {
    public static boolean validate(OrganizationTemplate template) {
        return template != null
                && validateAnnualTurnover(template.getAnnualTurnover())
                && validateEmployeesCount(template.getEmployeesCount())
                && validateFullName(template.getFullName())
                && validateRating(template.getRating())
                && validateType(template.getType())
                && validateOfficialAddress(template.getOfficialAddress())
                && validatePostalAddress(template.getPostalAddress());
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
        return officialAddress == null || AddressValidator.validate(officialAddress);
    }

    public static boolean validatePostalAddress(Address postalAddress) {
        return postalAddress == null || AddressValidator.validate(postalAddress);
    }
}