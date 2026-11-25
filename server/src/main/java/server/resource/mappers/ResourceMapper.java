package server.resource.mappers;

import server.exceptions.InvalidFieldFormatException;
import server.resource.entities.UserEntity;

public abstract class ResourceMapper<R, E, D> {
    public abstract E mapDTOToEntity(D dto);
    public E mapDTOToEntity(D dto, UserEntity user) {
        return mapDTOToEntity(dto);
    }

    public abstract void updateEntityFromDTO(E entity, D dto);

    public abstract R mapEntityToDomain(E entity);
}
