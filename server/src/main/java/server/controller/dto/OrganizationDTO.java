package server.controller.dto;

import common.domain.*;
import common.exceptions.InvalidFieldFormatException;
import server.service.auth.User;

public record OrganizationDTO(String name, Coordinates coordinates, Float annualTurnover,
                              String fullName, int employeesCount, OrganizationType type,
                              Address officialAddress, UserDTO user) {
    public static OrganizationDTO fromDomain(OrganizationTemplate template, User user) {
        return new OrganizationDTO(template.getName(), template.getCoordinates(),
                template.getAnnualTurnover(), template.getFullName(), template.getEmployeesCount(),
                template.getType(), template.getOfficialAddress(), UserDTO.fromDomain(user));
    }

    public User getUser() throws InvalidFieldFormatException {
        return user.toDomain();
    }

    public OrganizationTemplate toDomain() throws InvalidFieldFormatException {
        return new OrganizationTemplate.Builder()
                .setName(name())
                .setCoordinates(coordinates())
                .setAnnualTurnover(annualTurnover())
                .setFullName(fullName())
                .setEmployeesCount(employeesCount())
                .setType(type())
                .setOfficialAddress(officialAddress())
                .build();
    }
}
