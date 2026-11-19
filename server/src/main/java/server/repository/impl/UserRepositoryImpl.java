package server.repository.impl;

import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.model.User;
import server.repository.AUserRepository;
import server.service.user.Role;
import server.service.user.UserTemplate;

import java.util.List;

@ApplicationScoped
public class UserRepositoryImpl extends AUserRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public UserRepositoryImpl() {

    }

    @Override
    @Transactional
    public List<server.service.user.User> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);

        List<User> users = em.createQuery(query).getResultList();

        return users.stream()
                .map(User::toUser)
                .toList();
    }

    @Override
    @Transactional
    public server.service.user.User find(int id)
            throws RepositoryAccessFailedException, UserNotFoundException {
        User userDTO = em.find(User.class, id);
        if (userDTO == null) {
            throw new UserNotFoundException();
        }

        return userDTO.toUser();
    }

    @Override
    @Transactional
    public server.service.user.User findByName(String name)
            throws UserNotFoundException {
        User userDTO = findDTOByName(em, name);

        if (userDTO == null) {
            throw new UserNotFoundException();
        }

        return userDTO.toUser();
    }

    @Override
    @Transactional
    public void createSuperadmin(UserTemplate template)
            throws UserNameAlreadyTakenException {
        try {
            User userDTO = new User(template);
            server.service.user.User user = userDTO.toUser();
            user.setRole(Role.ROLE_SUPERADMIN);
            userDTO.update(user);

            em.persist(userDTO);

            em.createQuery("UPDATE User SET id = 0 WHERE username = 'root'")
                    .executeUpdate();
        } catch (PersistenceException e) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    @Transactional
    public void add(UserTemplate template)
            throws UserNameAlreadyTakenException {
        try {
            em.persist(new User(template));
        } catch (PersistenceException e) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    @Transactional
    public void update(server.service.user.User user)
            throws RepositoryAccessFailedException, UserNotFoundException {
        User userDTO = findDTOByName(em, user.getUsername());
        if (userDTO == null) {
            throw new UserNotFoundException();
        }

        userDTO.update(user);

        try {
            em.merge(userDTO);
        } catch (PersistenceException e) {
            throw new UserNotFoundException();
        }
    }

    private static User findDTOByName(EntityManager em, String name) {
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