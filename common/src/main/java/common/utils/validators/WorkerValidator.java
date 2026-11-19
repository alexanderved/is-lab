package common.utils.validators;

import java.util.Date;

import common.domain.Coordinates;
import common.domain.Organization;
import common.domain.Person;
import common.domain.Position;
import common.domain.Status;
import common.domain.Worker;

public final class WorkerValidator {
    public static boolean validate(Worker worker) {
        return worker != null
                && validateId(worker.getId())
                && validateName(worker.getName())
                && validateCoordinates(worker.getCoordinates())
                && validateCreationDate(worker.getCreationDate())
                && validateOrganization(worker.getOrganization())
                && validateSalary(worker.getSalary())
                && validateRating(worker.getRating())
                && validatePosition(worker.getPosition())
                && validateStatus(worker.getStatus())
                && validatePerson(worker.getPerson());
    }

    public static boolean validateId(int id) {
        return id > 0;
    }

    public static boolean validateName(String name) {
        return WorkerTemplateValidator.validateName(name);
    }

    public static boolean validateCoordinates(Coordinates coordinates) {
        return WorkerTemplateValidator.validateCoordinates(coordinates);
    }

    public static boolean validateCreationDate(Date creationDate) {
        return creationDate != null;
    }

    public static boolean validateOrganization(Organization organization) {
        return WorkerTemplateValidator.validateOrganization(organization);
    }

    public static boolean validateSalary(int salary) {
        return WorkerTemplateValidator.validateSalary(salary);
    }

    public static boolean validateRating(Long rating) {
        return WorkerTemplateValidator.validateRating(rating);
    }

    public static boolean validatePosition(Position position) {
        return WorkerTemplateValidator.validatePosition(position);
    }

    public static boolean validateStatus(Status status) {
        return WorkerTemplateValidator.validateStatus(status);
    }

    public static boolean validatePerson(Person person) {
        return WorkerTemplateValidator.validatePerson(person);
    }
}