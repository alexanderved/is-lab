package common.utils.validators;

import java.util.Date;

import common.domain.Coordinates;
import common.domain.Organization;
import common.domain.Person;
import common.domain.Position;
import common.domain.Status;
import common.domain.WorkerTemplate;

public final class WorkerTemplateValidator {
    public static boolean validate(WorkerTemplate template) {
        return template != null
                && validateName(template.getName())
                && validateCoordinates(template.getCoordinates())
                && validateOrganization(template.getOrganization())
                && validateSalary(template.getSalary())
                && validateRating(template.getRating())
                && validatePosition(template.getPosition())
                && validateStatus(template.getStatus())
                && validatePerson(template.getPerson());
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean validateCoordinates(Coordinates coordinates) {
        return coordinates != null;
    }

    public static boolean validateOrganization(Organization organization) {
        return organization == null || OrganizationValidator.validate(organization);
    }

    public static boolean validateSalary(int salary) {
        return salary > 0;
    }

    public static boolean validateRating(Long rating) {
        return rating == null || rating > 0;
    }

    public static boolean validatePosition(Position position) {
        return true; // nullable
    }

    public static boolean validateStatus(Status status) {
        return true; // nullable
    }

    public static boolean validatePerson(Person person) {
        return PersonValidator.validate(person);
    }
}