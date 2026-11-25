package server.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.*;
import server.exceptions.InvalidFieldFormatException;
import server.exceptions.UniquenessConstraintViolationException;
import server.resource.domain.organization.Organization;
import server.resource.domain.person.Person;
import server.resource.domain.worker.Position;
import server.resource.domain.worker.Worker;
import server.resource.dto.WorkerDTO;
import server.resource.entities.ImportOperationEntity;
import server.resource.entities.OrganizationEntity;
import server.resource.entities.UserEntity;
import server.resource.entities.WorkerEntity;
import server.resource.mappers.WorkerMapper;
import server.resource.validators.WorkerValidator;
import server.service.token.Token;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class WorkerRepository extends BulkResourceRepository<Worker, WorkerEntity, WorkerDTO,
        WorkerMapper, WorkerValidator> {
    public WorkerRepository() {
        entityClass = WorkerEntity.class;
        exceptionMessage = "Человек с данным id уже занимает данную должность в заданной организации";
    }

    public int countWorkersWithOrganizationPositionPerson(Organization organization, Position position, Person person) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<WorkerEntity> root = query.from(WorkerEntity.class);

        Predicate predicate = builder.and(
                builder.equal(root.get("organization").get("id"), organization.getId()),
                builder.equal(root.get("position"), position),
                builder.equal(root.get("person").get("id"), person.getId())
        );
        query.select(builder.count(root)).where(predicate);

        long result = em.createQuery(query).getSingleResult();
        return (int) result;
    }

    public Worker hire(int workerId, int organizationId, Token token) {
        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return null;
        }

        WorkerEntity worker = em.find(WorkerEntity.class, workerId);
        if (worker == null || isProtected(user, worker)) {
            return null;
        }

        OrganizationEntity org = organizationId > 0
                ? em.find(OrganizationEntity.class, organizationId)
                : null;
        if (organizationId > 0 && org == null) {
            return null;
        }

        worker.hire(org);
        em.flush();

        Worker res = mapper.mapEntityToDomain(worker);
        if (!validator.isValid(res)) {
            throw new UniquenessConstraintViolationException(exceptionMessage);
        }

        return res;
    }

    public Worker indexSalary(int workerId, double coefficient, Token token) {
        if (coefficient <= 0) {
            return null;
        }

        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return null;
        }

        WorkerEntity worker = em.find(WorkerEntity.class, workerId);
        if (worker == null || isProtected(user, worker)) {
            return null;
        }

        worker.indexSalary(coefficient);
        em.flush();

        Worker res = mapper.mapEntityToDomain(worker);
        if (!validator.isValid(res)) {
            throw new UniquenessConstraintViolationException(exceptionMessage);
        }

        return res;
    }
}
