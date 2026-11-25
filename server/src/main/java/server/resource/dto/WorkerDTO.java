package server.resource.dto;

import server.resource.domain.worker.Position;
import server.resource.domain.worker.Status;

public record WorkerDTO(
        String name,
        CoordinatesDTO coordinates,

        Integer organizationId,
        OrganizationDTO organization,

        int salary,
        Long rating,
        Position position,
        Status status,

        Integer personId,
        PersonDTO person
) {
}
