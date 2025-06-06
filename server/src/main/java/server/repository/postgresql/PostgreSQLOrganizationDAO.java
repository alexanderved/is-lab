package server.repository.postgresql;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import common.exceptions.InvalidFieldFormatException;
import org.hibernate.Session;
import server.service.token.Token;
import server.repository.OrganizationDAO;
import server.repository.postgresql.dto.OrganizationDTO;
import server.repository.postgresql.dto.UserDTO;

import java.util.List;
import java.util.Objects;

public class PostgreSQLOrganizationDAO extends OrganizationDAO {
    private PostgreSQLContext ctx;

    public PostgreSQLOrganizationDAO(PostgreSQLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<Organization> load() {
        return ctx.getSessionFactory().fromTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(OrganizationDTO.class);
            var root = query.from(OrganizationDTO.class);
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
    public Organization add(OrganizationTemplate template, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            UserDTO userDTO = getUserDTOByName(session, token.getUsername());
            if (userDTO == null) {
                return null;
            }

            OrganizationDTO organizationDTO = new OrganizationDTO(template, userDTO);
            session.persist(organizationDTO);

            try {
                return organizationDTO.toDomain();
            } catch (InvalidFieldFormatException e) {
                return null;
            }
        });
    }

    @Override
    public boolean remove(int id, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            OrganizationDTO organizationDTO = session.get(OrganizationDTO.class, id);
            if (organizationDTO == null
                    || organizationDTO.getUser().getName().equals(token.getUsername())) {
                 return false;
            }
            session.remove(organizationDTO);

            return true;
        });
    }

    @Override
    public boolean update(int id, OrganizationTemplate template, Token token) {
        return ctx.getSessionFactory().fromTransaction(session -> {
            OrganizationDTO organizationDTO = session.get(OrganizationDTO.class, id);
            if (organizationDTO == null
                    || organizationDTO.getUser().getName().equals(token.getUsername())) {
                return false;
            }
            organizationDTO.update(template);
            session.persist(organizationDTO);

            return true;
        });
    }

    private static UserDTO getUserDTOByName(Session session, String name) {
        var builder = session.getCriteriaBuilder();
        var query = builder.createQuery(UserDTO.class);
        var root = query.from(UserDTO.class);
        query.select(root)
                .where(builder.equal(root.get("name"), name));

        List<UserDTO> users = session.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}
