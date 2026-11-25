package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.dto.ImportOperationDTO;
import server.resource.entities.ImportOperationEntity;
import server.resource.entities.UserEntity;

@ApplicationScoped
public class ImportOperationMapper extends ResourceMapper<ImportOperation, ImportOperationEntity, ImportOperationDTO> {
    @Override
    public ImportOperationEntity mapDTOToEntity(ImportOperationDTO dto) {
        if (dto == null) {
            return null;
        }

        ImportOperationEntity entity = new ImportOperationEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public ImportOperationEntity mapDTOToEntity(ImportOperationDTO dto, UserEntity user) {
        if (dto == null) {
            return null;
        }

        ImportOperationEntity entity = new ImportOperationEntity();

        updateEntityFromDTO(entity, dto);
        entity.setUser(user);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(ImportOperationEntity entity, ImportOperationDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setStatus(dto.status());
        entity.setObjectsCount(dto.objectsCount());
    }

    @Override
    public ImportOperation mapEntityToDomain(ImportOperationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ImportOperation(entity.getId(), entity.getStatus(),
                entity.getObjectsCount(), entity.getUser().getId());
    }
}
