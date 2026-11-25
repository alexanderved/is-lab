package server.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.*;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.dto.ImportOperationDTO;
import server.resource.entities.ImportOperationEntity;
import server.resource.entities.UserEntity;
import server.resource.mappers.ImportOperationMapper;
import server.resource.validators.ImportOperationValidator;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ImportOperationRepository extends ResourceRepository<ImportOperation, ImportOperationEntity,
        ImportOperationDTO, ImportOperationMapper, ImportOperationValidator> {
    public ImportOperationRepository() {
        entityClass = ImportOperationEntity.class;
    }

    public List<ImportOperation> loadByUsername(String username) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ImportOperationEntity> query = builder.createQuery(entityClass);
        Root<ImportOperationEntity> root = query.from(entityClass);

        Join<ImportOperationEntity, UserEntity> userJoin = root.join("user");
        Predicate predicate = builder.equal(userJoin.get("username"), username);

        query.select(root).where(predicate);
        List<ImportOperationEntity> resultList = em.createQuery(query).getResultList();

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
}
