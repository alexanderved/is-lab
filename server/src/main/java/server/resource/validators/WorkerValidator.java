package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.repository.WorkerRepository;
import server.resource.domain.Coordinates;
import server.resource.domain.organization.Organization;
import server.resource.domain.person.Person;
import server.resource.domain.worker.Position;
import server.resource.domain.worker.Status;
import server.resource.domain.worker.Worker;

import java.util.Date;

@ApplicationScoped
public class WorkerValidator extends ResourceValidator<Worker> {
    @Inject
    private OrganizationValidator organizationValidator;

    @Inject
    private PersonValidator personValidator;

    @Inject
    private WorkerRepository workerRepository;

    @Override
    public boolean isValid(Worker res) {
        return isValidInstance(res)
                && (res.getOrganization() == null || organizationValidator.isValid(res.getOrganization()))
                && personValidator.isValid(res.getPerson())
                && validateUniqueness(res);
    }

    public boolean validateUniqueness(Worker res) {
        if (res.getPosition() == null || res.getOrganization() == null) {
            return true;
        }

        return workerRepository.countWorkersWithOrganizationPositionPerson(res.getOrganization(),
                res.getPosition(), res.getPerson()) == 1;
    }

    public static boolean isValidInstance(Worker worker) {
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
        return name != null && !name.isEmpty();
    }

    public static boolean validateCoordinates(Coordinates coordinates) {
        return coordinates != null;
    }

    public static boolean validateCreationDate(Date creationDate) {
        return creationDate != null;
    }

    public static boolean validateOrganization(Organization organization) {
        return organization == null || OrganizationValidator.isValidInstance(organization);
    }

    public static boolean validateSalary(int salary) {
        return salary > 0;
    }

    public static boolean validateRating(Long rating) {
        return rating == null || rating > 0;
    }

    public static boolean validatePosition(Position position) {
        return true;
    }

    public static boolean validateStatus(Status status) {
        return true;
    }

    public static boolean validatePerson(Person person) {
        return PersonValidator.isValidInstance(person);
    }
}
