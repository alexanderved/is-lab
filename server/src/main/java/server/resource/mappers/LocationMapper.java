package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Location;
import server.resource.dto.LocationDTO;
import server.resource.entities.LocationEntity;

@ApplicationScoped
public class LocationMapper extends ResourceMapper<Location, LocationEntity, LocationDTO> {
    @Override
    public LocationEntity mapDTOToEntity(LocationDTO dto) {
        if (dto == null) {
            return null;
        }

        LocationEntity entity = new LocationEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(LocationEntity entity, LocationDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setX(dto.x());
        entity.setY(dto.y());
        entity.setZ(dto.z());
        entity.setName(dto.name());
    }

    @Override
    public Location mapEntityToDomain(LocationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Location(entity.getX(), entity.getY(), entity.getZ(), entity.getName());
    }
}
