package server.repository;

import jakarta.enterprise.context.ApplicationScoped;
import server.resource.domain.person.Person;
import server.resource.dto.PersonDTO;
import server.resource.entities.PersonEntity;
import server.resource.mappers.PersonMapper;
import server.resource.validators.PersonValidator;

@ApplicationScoped
public class PersonRepository extends BulkResourceRepository<Person, PersonEntity, PersonDTO,
        PersonMapper, PersonValidator> {
    public PersonRepository() {
        entityClass = PersonEntity.class;
    }
}
