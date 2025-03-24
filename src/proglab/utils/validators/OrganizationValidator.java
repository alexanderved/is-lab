package proglab.utils.validators;

import java.time.ZonedDateTime;

import proglab.domain.Address;
import proglab.domain.Coordinates;
import proglab.domain.Organization;
import proglab.domain.OrganizationType;

public final class OrganizationValidator {
    public static boolean validate(Organization org) {
        return org != null
            && validateId(org.getId())
            && validateName(org.getName())
            && validateCoordinates(org.getCoordinates())
            && validateCreationDate(org.getCreationDate())
            && validateAnnualTurnover(org.getAnnualTurnover())
            && validateEmployeesCount(org.getEmployeesCount())
            && validateType(org.getType())
            && validateOfficialAddress(org.getOfficialAddress());
    }

    public static boolean validateId(int id) {
        return id > 0;
    }

    public static boolean validateName(String name) {
        return OrganizationTemplateValidator.validateName(name);
    }

    public static boolean validateCoordinates(Coordinates coordinates) {
        return OrganizationTemplateValidator.validateCoordinates(coordinates);
    }

    public static boolean validateCreationDate(ZonedDateTime creationDate) {
        return creationDate != null;
    }

    public static boolean validateAnnualTurnover(Float annualTurnover) {
        return OrganizationTemplateValidator.validateAnnualTurnover(annualTurnover);
    }

    public static boolean validateEmployeesCount(int employeesCount) {
        return OrganizationTemplateValidator.validateEmployeesCount(employeesCount);
    }

    public static boolean validateType(OrganizationType type) {
        return OrganizationTemplateValidator.validateType(type);
    }

    public static boolean validateOfficialAddress(Address officialAddress) {
        return OrganizationTemplateValidator.validateOfficialAddress(officialAddress);
    }
}
