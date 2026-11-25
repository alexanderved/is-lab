package server.resource.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.person.Person;
import server.resource.domain.user.User;
import server.resource.dto.PersonDTO;
import server.resource.entities.PersonEntity;
import server.resource.entities.UserEntity;

@ApplicationScoped
public class PersonMapper extends ResourceMapper<Person, PersonEntity, PersonDTO> {
    @Inject
    private LocationMapper locationMapper;

    @Override
    public PersonEntity mapDTOToEntity(PersonDTO dto) {
        if (dto == null) {
            return null;
        }

        PersonEntity entity = new PersonEntity();
        updateEntityFromDTO(entity, dto);

        return entity;
    }

    @Override
    public PersonEntity mapDTOToEntity(PersonDTO dto, UserEntity user) {
        if (dto == null) {
            return null;
        }

        PersonEntity entity = new PersonEntity();
        updateEntityFromDTO(entity, dto);
        entity.setUser(user);

        return entity;
    }

    @Override
    public void updateEntityFromDTO(PersonEntity entity, PersonDTO dto) {
        if (dto == null) {
            return;
        }

        entity.setEyeColor(dto.eyeColor());
        entity.setHairColor(dto.hairColor());
        entity.setLocation(locationMapper.mapDTOToEntity(dto.location()));
        entity.setBirthday(dto.birthday());
        entity.setWeight(dto.weight());
        entity.setNationality(dto.nationality());
    }

    @Override
    public Person mapEntityToDomain(PersonEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Person.Builder()
                .setId(entity.getId())
                .setEyeColor(entity.getEyeColor())
                .setHairColor(entity.getHairColor())
                .setLocation(locationMapper.mapEntityToDomain(entity.getLocation()))
                .setBirthday(entity.getBirthday())
                .setWeight(entity.getWeight())
                .setNationality(entity.getNationality())
                .setUserId(entity.getUser().getId())
                .build();
    }
}
