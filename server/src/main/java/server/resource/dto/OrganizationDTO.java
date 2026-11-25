package server.resource.dto;

import server.resource.domain.organization.OrganizationType;

public record OrganizationDTO(
        AddressDTO officialAddress,
        Long annualTurnover,
        Long employeesCount,
        String fullName,
        Float rating,
        OrganizationType type,
        AddressDTO postalAddress
) {
}
