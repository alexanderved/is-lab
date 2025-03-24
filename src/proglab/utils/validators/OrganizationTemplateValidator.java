package proglab.utils.validators;

import proglab.domain.Address;
import proglab.domain.Coordinates;
import proglab.domain.OrganizationTemplate;
import proglab.domain.OrganizationType;

public final class OrganizationTemplateValidator {
    public static boolean validate(OrganizationTemplate template) {
        return template != null
            && validateName(template.getName())
            && validateCoordinates(template.getCoordinates())
            && validateAnnualTurnover(template.getAnnualTurnover())
            && validateEmployeesCount(template.getEmployeesCount())
            && validateType(template.getType())
            && validateOfficialAddress(template.getOfficialAddress());
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean validateCoordinates(Coordinates coordinates) {
        return CoordinatesValidator.validate(coordinates);
    }

    public static boolean validateAnnualTurnover(Float annualTurnover) {
        return annualTurnover == null || annualTurnover > 0;
    }

    public static boolean validateEmployeesCount(int employeesCount) {
        return employeesCount > 0;
    }

    public static boolean validateType(OrganizationType type) {
        return type != null;
    }

    public static boolean validateOfficialAddress(Address officialAddress) {
        return officialAddress == null || AddressValidator.validate(officialAddress);
    }
}
