package server.service.collection;

import java.util.List;
import jakarta.transaction.Transactional;
import server.repository.PersonRepository;
import server.resource.domain.person.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.resource.dto.PersonDTO;
import server.service.token.Token;

@ApplicationScoped
public class PersonCollectionService {
    @Inject
    private PersonRepository personRepository;

    @Inject
    private WorkerCollectionService workerCollectionService;

    public PersonCollectionService() {

    }

    @Transactional
    public List<Person> getCollection(String order,
                                      String column,
                                      int pageNumber,
                                      int perPageNumber,
                                      int pageCount,
                                      String filter) {
        List<Person> collection = personRepository.load();

        long skip = Math.max(0L, (long) (pageNumber - 1) * perPageNumber);
        long limit = (long) Math.max(0, pageCount) * perPageNumber;

        return collection.stream()
                .filter((person) -> matchesFilter(person, column, filter))
                .sorted((a, b) -> comparePersons(a, b, column, order))
                .skip(skip)
                .limit(limit)
                .toList();
    }

    @Transactional
    public int count(String column, String filter) {
        return (int) personRepository.load()
                .stream()
                .filter((person) -> matchesFilter(person, column, filter))
                .count();
    }

    private static boolean matchesFilter(Person person, String column, String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        String value = switch (column) {
            case "id" -> String.valueOf(person.getId());
            case "eyeColor" -> person.getEyeColor() == null
                    ? null : person.getEyeColor().toString();
            case "hairColor" -> person.getHairColor() == null
                    ? null : person.getHairColor().toString();
            case "location" -> person.getLocation() == null ? null : person.getLocation().getName();
            case "birthday" -> person.getBirthday() == null
                    ? null : String.valueOf(person.getBirthday());
            case "weight" -> String.valueOf(person.getWeight());
            case "nationality" -> person.getNationality() == null
                    ? null : person.getNationality().toString();
            default -> null;
        };

        return value != null && value.contains(filter);
    }

    private static int comparePersons(Person a, Person b, String field, String order) {
        int result = switch (field) {
            case "id" -> Integer.compare(a.getId(), b.getId());
            case "eyeColor" -> a.getEyeColor().compareTo(b.getEyeColor());
            case "hairColor" -> a.getHairColor().compareTo(b.getHairColor());
            case "location" -> {
                String la = a.getLocation() == null ? null : a.getLocation().getName();
                String lb = b.getLocation() == null ? null : b.getLocation().getName();
                yield compareNullable(la, lb);
            }
            case "birthday" -> compareNullable(a.getBirthday(), b.getBirthday());
            case "weight" -> Integer.compare(a.getWeight(), b.getWeight());
            case "nationality" -> compareNullable(a.getNationality(), b.getNationality());
            default -> 0;
        };

        return order.equals("ASC") ? result : -result;
    }

    private static <T extends Comparable<T>> int compareNullable(T a, T b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }

        return a.compareTo(b);
    }

    @Transactional
    public int add(Token token, PersonDTO dto) {
        return personRepository.add(dto, token).getId();
    }

    @Transactional
    public boolean removeById(Token token, int id) {
        return personRepository.remove(id, token);
    }

    @Transactional
    public boolean update(Token token, int id, PersonDTO dto) {
        return personRepository.update(id, dto, token);
    }
}