package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.worker.Worker;
import server.resource.dto.WorkerDTO;
import server.resource.entities.OrganizationEntity;
import server.resource.entities.PersonEntity;
import server.resource.entities.UserEntity;
import server.resource.entities.WorkerEntity;

import java.util.Date;

@ApplicationScoped
public class WorkerMapper extends ResourceMapper<Worker, WorkerEntity, WorkerDTO> {
    @Inject
    private CoordinatesMapper coordinatesMapper;

    @Inject
    private OrganizationMapper organizationMapper;

    @Inject
    private PersonMapper personMapper;

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public WorkerEntity mapDTOToEntity(WorkerDTO dto) {
        if (dto == null) {
            return null;
        }

        WorkerEntity entity = new WorkerEntity();
        updateEntityFromDTO(entity, dto);

        entity.setCreationDate(new Date());

        return entity;
    }

    @Override
    public WorkerEntity mapDTOToEntity(WorkerDTO dto, UserEntity user) {
        if (dto == null) {
            return null;
        }

        WorkerEntity entity = new WorkerEntity();
        updateEntityFromDTO(entity, dto);

        entity.setCreationDate(new Date());
        entity.setUser(user);

        if (dto.organizationId() == null && entity.getOrganization() != null) {
            entity.getOrganization().setUser(user);
        }

        if (dto.personId() == null && entity.getPerson() != null) {
            entity.getPerson().setUser(user);
        }

        return entity;
    }

    @Override
    public void updateEntityFromDTO(WorkerEntity entity, WorkerDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setName(dto.name());
        entity.setCoordinates(coordinatesMapper.mapDTOToEntity(dto.coordinates()));

        if (dto.organizationId() != null) {
            entity.setOrganization(em.getReference(OrganizationEntity.class, dto.organizationId()));
        } else if (dto.organization() != null) {
            entity.setOrganization(organizationMapper.mapDTOToEntity(dto.organization()));
        }

        entity.setSalary(dto.salary());
        entity.setRating(dto.rating());
        entity.setPosition(dto.position());
        entity.setStatus(dto.status());

        if (dto.personId() != null) {
            entity.setPerson(em.getReference(PersonEntity.class, dto.personId()));
        } else if (dto.person() != null) {
            entity.setPerson(personMapper.mapDTOToEntity(dto.person()));
        }
    }

    @Override
    public Worker mapEntityToDomain(WorkerEntity entity) throws InvalidFieldFormatException {
        if (entity == null) {
            return null;
        }

        return new Worker.Builder()
                .setId(entity.getId())
                .setName(entity.getName())
                .setCoordinates(coordinatesMapper.mapEntityToDomain(entity.getCoordinates()))
                .setCreationDate(entity.getCreationDate())
                .setOrganization(organizationMapper.mapEntityToDomain(entity.getOrganization()))
                .setSalary(entity.getSalary())
                .setRating(entity.getRating())
                .setPosition(entity.getPosition())
                .setStatus(entity.getStatus())
                .setPerson(personMapper.mapEntityToDomain(entity.getPerson()))
                .setUserId(entity.getUser().getId())
                .build();
    }
}
