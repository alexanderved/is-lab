package server.repository.impl;

import common.domain.WorkerTemplate;
import common.exceptions.InvalidFieldFormatException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import server.model.Organization;
import server.model.Person;
import server.repository.AWorkerRepository;
import server.service.token.Token;
import server.model.Worker;
import server.model.User;
import server.service.user.Role;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class WorkerRepositoryImpl extends AWorkerRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public WorkerRepositoryImpl() {

    }

    @Override
    @Transactional
    public List<common.domain.Worker> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Worker> query = builder.createQuery(Worker.class);
        Root<Worker> root = query.from(Worker.class);
        query.select(root);

        List<Worker> resultList = em.createQuery(query).getResultList();

        return resultList.stream()
                .map(dto -> {
                    try {
                        return dto.toDomain();
                    } catch (InvalidFieldFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public common.domain.Worker add(WorkerTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return null;
        }

        Organization org = template.getOrganization() == null
                ? null
                : em.getReference(Organization.class, template.getOrganization().getId());
        Person person = em.getReference(
                Person.class, template.getPerson().getId());

        Worker worker = new Worker(template, org, person, user);
        em.persist(worker);
        em.flush();

        try {
            return worker.toDomain();
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean remove(int id, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return false;
        }

        Worker worker = em.find(Worker.class, id);
        if (worker == null) {
            return false;
        }

        if (canModify(user, worker)) {
            em.remove(worker);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean update(int id, WorkerTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return false;
        }

        Worker worker = em.find(Worker.class, id);
        if (worker == null) {
            return false;
        }

        if (canModify(user, worker)) {
            Organization org = template.getOrganization() == null
                    ? null
                    : em.getReference(Organization.class, template.getOrganization().getId());
            Person person = em.getReference(
                    Person.class, template.getPerson().getId());

            worker.update(template, org, person);
        }

        return true;
    }

    private static User getUserDTOByName(EntityManager em, String name) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root)
                .where(builder.equal(root.get("username"), name));

        List<User> users = em.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    @Transactional
    public common.domain.Worker hire(int workerId, int organizationId, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return null;
        }

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null || !canModify(user, worker)) {
            return null;
        }

        Organization org = organizationId > 0
                ? em.find(Organization.class, organizationId)
                : null;
        if (organizationId > 0 && org == null) {
            return null;
        }

        worker.hire(org);
        em.flush();

        try {
            return worker.toDomain();
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public common.domain.Worker indexSalary(int workerId, double coefficient, Token token) {
        if (coefficient <= 0) {
            return null;
        }

        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return null;
        }

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null || !canModify(user, worker)) {
            return null;
        }

        worker.indexSalary(coefficient);
        em.flush();

        try {
            return worker.toDomain();
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    private static boolean canModify(User user, Worker worker) {
        return user.getRole() == Role.ROLE_SUPERADMIN
                || (user.getRole() == Role.ROLE_ADMIN
                && worker.getUser().getRole() != Role.ROLE_SUPERADMIN)
                || worker.getUser().getUsername().equals(user.getUsername());
    }
}