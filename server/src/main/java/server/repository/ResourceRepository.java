package server.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import server.exceptions.InvalidFieldFormatException;
import server.exceptions.UniquenessConstraintViolationException;
import server.resource.domain.user.Role;
import server.resource.entities.*;
import server.resource.mappers.ResourceMapper;
import server.resource.validators.ResourceValidator;
import server.service.token.Token;

import java.util.List;
import java.util.Objects;

public abstract class ResourceRepository<R, E extends OwnedByUser, D,
        M extends ResourceMapper<R, E, D>, V extends ResourceValidator<R>> {
    protected Class<E> entityClass;

    protected String exceptionMessage = "";

    @PersistenceContext(unitName = "default")
    protected EntityManager em;

    @Inject
    protected M mapper;

    @Inject
    protected V validator;

    public List<R> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(entityClass);
        Root<E> root = query.from(entityClass);
        query.select(root);

        List<E> resultList = em.createQuery(query).getResultList();

        return resultList.stream()
                .map(entity -> {
                    try {
                        return mapper.mapEntityToDomain(entity);
                    } catch (InvalidFieldFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public R add(D dto, Token token) {
        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return null;
        }

        E entity = mapper.mapDTOToEntity(dto, user);
        em.persist(entity);
        em.flush();

        R res = mapper.mapEntityToDomain(entity);
        if (!validator.isValid(res)) {
            throw new UniquenessConstraintViolationException(exceptionMessage);
        }

        return res;
    }

    public boolean remove(int id, Token token) {
        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return false;
        }

        E entity = em.find(entityClass, id);
        if (entity == null || isProtected(user, entity)) {
            return false;
        }

        em.remove(entity);
        return true;
    }

    public boolean update(int id, D dto, Token token) {
        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return false;
        }

        E entity = em.find(entityClass, id);
        if (entity == null || isProtected(user, entity)) {
            return false;
        }

        mapper.updateEntityFromDTO(entity, dto);
        em.merge(entity);
        em.flush();

        R res = mapper.mapEntityToDomain(entity);
        if (!validator.isValid(res)) {
            throw new UniquenessConstraintViolationException(exceptionMessage);
        }

        return true;
    }

    protected UserEntity getUserDTOByName(String name) {
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

    protected boolean isProtected(UserEntity user, E entity) {
        return user.getRole() != Role.ROLE_SUPERADMIN
                && (user.getRole() != Role.ROLE_ADMIN
                || entity.getUser().getRole() == Role.ROLE_SUPERADMIN)
                && !entity.getUser().getUsername().equals(user.getUsername());
    }
}
