package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Coordinates;
import server.resource.dto.CoordinatesDTO;
import server.resource.entities.CoordinatesEntity;

@ApplicationScoped
public class CoordinatesMapper extends ResourceMapper<Coordinates, CoordinatesEntity, CoordinatesDTO> {
    @Override
    public CoordinatesEntity mapDTOToEntity(CoordinatesDTO dto) {
        if (dto == null) {
            return null;
        }

        CoordinatesEntity entity = new CoordinatesEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(CoordinatesEntity entity, CoordinatesDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setX(dto.x());
        entity.setY(dto.y());
    }

    @Override
    public Coordinates mapEntityToDomain(CoordinatesEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Coordinates(entity.getX(), entity.getY());
    }
}
