package server.repository;

import server.exceptions.UserNameAlreadyTakenException;
import server.exceptions.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.resource.entities.UserEntity;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;
import server.resource.domain.user.UserTemplate;

import java.util.List;

@ApplicationScoped
public class UserRepositoryImpl extends UserRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public UserRepositoryImpl() {

    }

    @Override
    @Transactional
    public List<User> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        query.select(root);

        List<UserEntity> users = em.createQuery(query).getResultList();

        return users.stream()
                .map(UserEntity::toUser)
                .toList();
    }

    @Override
    @Transactional
    public User find(int id) throws UserNotFoundException {
        UserEntity userDTO = em.find(UserEntity.class, id);
        if (userDTO == null) {
            throw new UserNotFoundException();
        }

        return userDTO.toUser();
    }

    @Override
    @Transactional
    public User findByName(String name)
            throws UserNotFoundException {
        UserEntity userDTO = findDTOByName(em, name);

        if (userDTO == null) {
            throw new UserNotFoundException();
        }

        return userDTO.toUser();
    }

    @Override
    @Transactional
    public void createSuperadmin(UserTemplate template) throws UserNameAlreadyTakenException {
        try {
            UserEntity userDTO = new UserEntity(template);
            User user = userDTO.toUser();
            user.setRole(Role.ROLE_SUPERADMIN);
            userDTO.update(user);

            em.persist(userDTO);

            em.createQuery("UPDATE UserEntity SET id = 0 WHERE username = 'root'")
                    .executeUpdate();
        } catch (PersistenceException e) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    @Transactional
    public void add(UserTemplate template) throws UserNameAlreadyTakenException {
        try {
            em.persist(new UserEntity(template));
        } catch (PersistenceException e) {
            throw new UserNameAlreadyTakenException(template.getUsername());
        }
    }

    @Override
    @Transactional
    public void update(User user) throws UserNotFoundException {
        UserEntity userDTO = findDTOByName(em, user.getUsername());
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

    private static UserEntity findDTOByName(EntityManager em, String name) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        query.select(root)
                .where(builder.equal(root.get("username"), name));

        List<UserEntity> users = em.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}