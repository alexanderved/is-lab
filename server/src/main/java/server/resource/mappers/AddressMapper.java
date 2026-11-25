package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Address;
import server.resource.dto.AddressDTO;
import server.resource.entities.AddressEntity;

@ApplicationScoped
public class AddressMapper extends ResourceMapper<Address, AddressEntity, AddressDTO> {
    @Inject
    private LocationMapper locationMapper;

    @Override
    public AddressEntity mapDTOToEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        AddressEntity entity = new AddressEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(AddressEntity entity, AddressDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setStreet(dto.street());
        entity.setTown(locationMapper.mapDTOToEntity(dto.town()));
    }

    @Override
    public Address mapEntityToDomain(AddressEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Address(entity.getStreet(), locationMapper.mapEntityToDomain(entity.getTown()));
    }
}
