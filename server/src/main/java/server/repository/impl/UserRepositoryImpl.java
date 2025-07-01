package server.repository.impl;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import server.config.HibernateConfiguration;
import server.exceptions.RepositoryAccessFailedException;
import server.model.User;
import server.repository.AUserRepository;
import server.service.user.Role;
import server.service.user.UserTemplate;

import java.util.List;

public class UserRepositoryImpl extends AUserRepository {
    private final HibernateConfiguration ctx;

    public UserRepositoryImpl(HibernateConfiguration ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<server.service.user.User> load() {
        return ctx.getSessionFactory().fromTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(User.class);
            var root = query.from(User.class);
            query.select(root);

            List<User> users = session.createQuery(query).getResultList();

            return users.stream()
                    .map(User::toUser)
                    .toList();
        });
    }

    @Override
    public server.service.user.User find(int id)
            throws RepositoryAccessFailedException, UserNotFoundException {
        server.service.user.User user = ctx.getSessionFactory().fromTransaction(session -> {
            User userDTO = session.get(User.class, id);
            if (userDTO == null) {
                return null;
            }

            return userDTO.toUser();
        });

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public server.service.user.User findByName(String name)
            throws UserNotFoundException {
        server.service.user.User user = ctx.getSessionFactory().fromTransaction(session -> {
            User userDTO = findDTOByName(session, name);
            if (userDTO == null) {
                return null;
            }

            return userDTO.toUser();
        });

        if (user == null) {
             throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public void createSuperadmin(UserTemplate template)
            throws UserNameAlreadyTakenException {
        boolean isAdded = ctx.getSessionFactory().fromTransaction(session -> {
            try {
                var userDTO = new User(template);

                var user = userDTO.toUser();
                user.setRole(Role.ROLE_SUPERADMIN);
                userDTO.update(user);

                session.persist(userDTO);
                session.createMutationQuery("UPDATE User SET id = 0 WHERE username = 'root'")
                            .executeUpdate();
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isAdded) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    public void add(UserTemplate template)
            throws UserNameAlreadyTakenException {
        boolean isAdded = ctx.getSessionFactory().fromTransaction(session -> {
            try {
                session.persist(new User(template));
            } catch (ConstraintViolationException e) {
                return false;
            }

            return true;
        });

        if (!isAdded) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    public void update(server.service.user.User user)
            throws RepositoryAccessFailedException, UserNotFoundException {
        boolean isUpdated = ctx.getSessionFactory().fromTransaction(session -> {
            User userDTO = findDTOByName(session, user.getUsername());
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

    private User findDTOByName(Session session, String name) {
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
