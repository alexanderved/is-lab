package server.repository.impl;

import common.domain.OrganizationTemplate;
import common.exceptions.InvalidFieldFormatException;
import org.hibernate.Session;
import server.config.HibernateConfiguration;
import server.repository.AOrganizationRepository;
import server.service.token.Token;
import server.model.Organization;
import server.model.User;
import server.service.user.Role;

import java.util.List;
import java.util.Objects;

public class OrganizationRepositoryImpl extends AOrganizationRepository {
    private HibernateConfiguration ctx;

    public OrganizationRepositoryImpl(HibernateConfiguration ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<common.domain.Organization> load() {
        return ctx.getSessionFactory().fromTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(Organization.class);
            var root = query.from(Organization.class);
            query.select(root);

            return session.createQuery(query)
                    .getResultList()
                    .stream()
                    .map((dto) -> {
                        try {
                            return dto.toDomain();
                        } catch (InvalidFieldFormatException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        });
    }

    @Override
    public common.domain.Organization add(OrganizationTemplate template, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            User user = getUserDTOByName(session, token.getUsername());
            if (user == null) {
                return null;
            }

            Organization organization = new Organization(template, user);
            session.persist(organization);

            try {
                return organization.toDomain();
            } catch (InvalidFieldFormatException e) {
                return null;
            }
        });
    }

    @Override
    public boolean remove(int id, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            User user = getUserDTOByName(session, token.getUsername());
            if (user == null) {
                return null;
            }

            Organization organization = session.get(Organization.class, id);
            if (organization == null) {
                 return false;
            }

            if ((user.getRole() == Role.ROLE_ADMIN
                    && organization.getUser().getRole() != Role.ROLE_SUPERADMIN)
                    || user.getRole() == Role.ROLE_SUPERADMIN
                    || organization.getUser().getUsername().equals(user.getUsername())) {
                session.remove(organization);
            }

            return true;
        });
    }

    @Override
    public boolean update(int id, OrganizationTemplate template, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            User user = getUserDTOByName(session, token.getUsername());
            if (user == null) {
                return null;
            }

            Organization organization = session.get(Organization.class, id);
            if (organization == null) {
                return false;
            }

            if ((user.getRole() == Role.ROLE_ADMIN
                    && organization.getUser().getRole() != Role.ROLE_SUPERADMIN)
                    || user.getRole() == Role.ROLE_SUPERADMIN
                    || organization.getUser().getUsername().equals(user.getUsername())) {
                organization.update(template);
                session.persist(organization);
            }

            return true;
        });
    }

    private static User getUserDTOByName(Session session, String name) {
        var builder = session.getCriteriaBuilder();
        var query = builder.createQuery(User.class);
        var root = query.from(User.class);
        query.select(root)
                .where(builder.equal(root.get("username"), name));

        List<User> users = session.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}
