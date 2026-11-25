package server.repository;

import server.exceptions.InvalidFieldFormatException;
import server.resource.entities.OwnedByUser;
import server.resource.entities.UserEntity;
import server.resource.mappers.ResourceMapper;
import server.resource.validators.ResourceValidator;
import server.service.token.Token;

import java.util.ArrayList;
import java.util.List;

public abstract class BulkResourceRepository<R, E extends OwnedByUser, D,
        M extends ResourceMapper<R, E, D>, V extends ResourceValidator<R>>
        extends ResourceRepository<R, E, D, M, V> {
    public void bulkAdd(List<D> dtoList, Token token) {
        UserEntity user = getUserDTOByName(token.getUsername());
        if (user == null) {
            return;
        }

        List<E> entities = new ArrayList<>();
        for (var dto : dtoList) {
            E entity = mapper.mapDTOToEntity(dto, user);
            entities.add(entity);
            em.persist(entity);
        }

        em.flush();

        for (var entity : entities) {
            R res = mapper.mapEntityToDomain(entity);
            if (!validator.isValid(res)) {
                throw new InvalidFieldFormatException();
            }
        }
    }
}
