package server.resource.dto;

import server.resource.domain.import_operation.ImportOperationStatus;

public record ImportOperationDTO(ImportOperationStatus status, Integer objectsCount) {
}
