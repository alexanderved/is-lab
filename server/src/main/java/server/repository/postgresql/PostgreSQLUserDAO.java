package server.repository.postgresql;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import server.exceptions.RepositoryAccessFailedException;
import server.repository.postgresql.dto.RefreshTokenDTO;
import server.service.auth.HashedUser;
import server.repository.UserDAO;
import server.repository.postgresql.dto.UserDTO;

import java.util.List;

public class PostgreSQLUserDAO extends UserDAO {
    private PostgreSQLContext ctx;

    public PostgreSQLUserDAO(PostgreSQLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public HashedUser findByName(String name)
            throws UserNotFoundException {
        HashedUser user = ctx.getSessionFactory().fromTransaction(session -> {
            UserDTO userDTO = findDTOByName(session, name);
            if (userDTO == null) {
                return null;
            }

            return userDTO.toHashedUser();
        });

        if (user == null) {
             throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public void add(HashedUser user)
            throws UserNameAlreadyTakenException {
        boolean isAdded = ctx.getSessionFactory().fromTransaction(session -> {
            try {
                session.persist(new UserDTO(user));
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isAdded) {
            throw new UserNameAlreadyTakenException(user.getUsername());
        }
    }

    @Override
    public void update(HashedUser user) throws RepositoryAccessFailedException, UserNotFoundException {
        boolean isUpdated = ctx.getSessionFactory().fromTransaction(session -> {
            UserDTO userDTO = findDTOByName(session, user.getUsername());
            if (userDTO == null) {
                return false;
            }

            userDTO.update(user);
            try {
                session.merge(userDTO);
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isUpdated) {
            throw new UserNotFoundException();
        }
    }

    private UserDTO findDTOByName(Session session, String name) {
        var builder = session.getCriteriaBuilder();
        var query = builder.createQuery(UserDTO.class);
        var root = query.from(UserDTO.class);
        query.select(root)
                .where(builder.equal(root.get("name"), name));

        List<UserDTO> userDTOs = session.createQuery(query).getResultList();
        if (userDTOs.isEmpty()) {
            return null;
        }

        return userDTOs.get(0);
    }
}
