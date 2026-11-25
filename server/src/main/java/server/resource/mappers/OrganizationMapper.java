package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.organization.Organization;
import server.resource.dto.OrganizationDTO;
import server.resource.entities.OrganizationEntity;
import server.resource.entities.UserEntity;

@ApplicationScoped
public class OrganizationMapper extends ResourceMapper<Organization, OrganizationEntity, OrganizationDTO> {
    @Inject
    private AddressMapper addressMapper;

    @Override
    public OrganizationEntity mapDTOToEntity(OrganizationDTO dto) {
        if (dto == null) {
            return null;
        }

        OrganizationEntity entity = new OrganizationEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public OrganizationEntity mapDTOToEntity(OrganizationDTO dto, UserEntity user) {
        if (dto == null) {
            return null;
        }

        OrganizationEntity entity = new OrganizationEntity();
        updateEntityFromDTO(entity, dto);
        entity.setUser(user);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(OrganizationEntity entity, OrganizationDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setOfficialAddress(addressMapper.mapDTOToEntity(dto.officialAddress()));
        entity.setAnnualTurnover(dto.annualTurnover());
        entity.setEmployeesCount(dto.employeesCount());
        entity.setFullName(dto.fullName());
        entity.setRating(dto.rating());
        entity.setType(dto.type());
        entity.setPostalAddress(addressMapper.mapDTOToEntity(dto.postalAddress()));
    }

    @Override
    public Organization mapEntityToDomain(OrganizationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Organization.Builder()
                .setId(entity.getId())
                .setOfficialAddress(addressMapper.mapEntityToDomain(entity.getOfficialAddress()))
                .setAnnualTurnover(entity.getAnnualTurnover())
                .setEmployeesCount(entity.getEmployeesCount())
                .setFullName(entity.getFullName())
                .setRating(entity.getRating())
                .setType(entity.getType())
                .setPostalAddress(addressMapper.mapEntityToDomain(entity.getPostalAddress()))
                .setUserId(entity.getUser().getId())
                .build();
    }
}
