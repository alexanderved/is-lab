package server.repository.impl;

import common.domain.OrganizationTemplate;
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
import server.repository.AOrganizationRepository;
import server.service.token.Token;
import server.model.Organization;
import server.model.User;
import server.service.user.Role;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class OrganizationRepositoryImpl extends AOrganizationRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public OrganizationRepositoryImpl() {

    }

    @Override
    @Transactional
    public List<common.domain.Organization> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Organization> query = builder.createQuery(Organization.class);
        Root<Organization> root = query.from(Organization.class);
        query.select(root);

        List<Organization> resultList = em.createQuery(query).getResultList();

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
    public common.domain.Organization add(OrganizationTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return null;
        }

        Organization organization = new Organization(template, user);
        em.persist(organization);
        em.flush();

        try {
            return organization.toDomain();
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

        Organization organization = em.find(Organization.class, id);
        if (organization == null) {
            return false;
        }

        if ((user.getRole() == Role.ROLE_ADMIN
                && organization.getUser().getRole() != Role.ROLE_SUPERADMIN)
                || user.getRole() == Role.ROLE_SUPERADMIN
                || organization.getUser().getUsername().equals(user.getUsername())) {
            em.remove(organization);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean update(int id, OrganizationTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return false;
        }

        Organization organization = em.find(Organization.class, id);
        if (organization == null) {
            return false;
        }

        if ((user.getRole() == Role.ROLE_ADMIN
                && organization.getUser().getRole() != Role.ROLE_SUPERADMIN)
                || user.getRole() == Role.ROLE_SUPERADMIN
                || organization.getUser().getUsername().equals(user.getUsername())) {
            organization.update(template);
            em.merge(organization);
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
}