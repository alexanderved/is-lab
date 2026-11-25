package server.repository;

import jakarta.enterprise.context.ApplicationScoped;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.organization.Organization;
import server.resource.dto.OrganizationDTO;
import server.resource.entities.OrganizationEntity;
import server.resource.entities.UserEntity;
import server.resource.mappers.OrganizationMapper;
import server.resource.validators.OrganizationValidator;
import server.service.token.Token;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrganizationRepository
        extends BulkResourceRepository<Organization, OrganizationEntity, OrganizationDTO,
                OrganizationMapper, OrganizationValidator>
{
    public OrganizationRepository() {
        entityClass = OrganizationEntity.class;
    }
}
