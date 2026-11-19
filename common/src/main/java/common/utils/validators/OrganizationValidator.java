package common.utils.validators;

import common.domain.Address;
import common.domain.Organization;
import common.domain.OrganizationType;

public final class OrganizationValidator {
    public static boolean validate(Organization org) {
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
        return OrganizationTemplateValidator.validateAnnualTurnover(annualTurnover);
    }

    public static boolean validateEmployeesCount(Long employeesCount) {
        return OrganizationTemplateValidator.validateEmployeesCount(employeesCount);
    }

    public static boolean validateFullName(String fullName) {
        return OrganizationTemplateValidator.validateFullName(fullName);
    }

    public static boolean validateRating(Float rating) {
        return OrganizationTemplateValidator.validateRating(rating);
    }

    public static boolean validateType(OrganizationType type) {
        return OrganizationTemplateValidator.validateType(type);
    }

    public static boolean validateOfficialAddress(Address officialAddress) {
        return OrganizationTemplateValidator.validateOfficialAddress(officialAddress);
    }

    public static boolean validatePostalAddress(Address postalAddress) {
        return OrganizationTemplateValidator.validatePostalAddress(postalAddress);
    }
}