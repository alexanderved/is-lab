package server.repository.impl;

import common.domain.PersonTemplate;
import common.exceptions.InvalidFieldFormatException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import server.repository.APersonRepository;
import server.service.token.Token;
import server.model.Person;
import server.model.User;
import server.service.user.Role;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PersonRepositoryImpl extends APersonRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public PersonRepositoryImpl() {

    }

    @Override
    @Transactional
    public List<common.domain.Person> load() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        Root<Person> root = query.from(Person.class);
        query.select(root);

        List<Person> resultList = em.createQuery(query).getResultList();

        return resultList.stream()
                .map(dto -> {
                    try {
                        return dto.toDomain();
                    } catch (InvalidFieldFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public common.domain.Person add(PersonTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return null;
        }

        Person person = new Person(template, user);
        em.persist(person);
        em.flush();

        try {
            return person.toDomain();
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean remove(int id, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return false;
        }

        Person person = em.find(Person.class, id);
        if (person == null) {
            return false;
        }

        if ((user.getRole() == Role.ROLE_ADMIN
                && person.getUser().getRole() != Role.ROLE_SUPERADMIN)
                || user.getRole() == Role.ROLE_SUPERADMIN
                || person.getUser().getUsername().equals(user.getUsername())) {
            em.remove(person);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean update(int id, PersonTemplate template, Token token) {
        User user = getUserDTOByName(em, token.getUsername());
        if (user == null) {
            return false;
        }

        Person person = em.find(Person.class, id);
        if (person == null) {
            return false;
        }

        if ((user.getRole() == Role.ROLE_ADMIN
                && person.getUser().getRole() != Role.ROLE_SUPERADMIN)
                || user.getRole() == Role.ROLE_SUPERADMIN
                || person.getUser().getUsername().equals(user.getUsername())) {
            person.update(template);
        }

        return true;
    }

    private static User getUserDTOByName(EntityManager em, String name) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root)
                .where(builder.equal(root.get("username"), name));

        List<User> users = em.createQuery(query).getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }
}