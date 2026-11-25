package server.resource.dto;

import server.resource.domain.person.Color;
import server.resource.domain.person.Country;

import java.util.Date;

public record PersonDTO(
        Color eyeColor,
        Color hairColor,
        LocationDTO location,
        Date birthday,
        int weight,
        Country nationality
) {
}
