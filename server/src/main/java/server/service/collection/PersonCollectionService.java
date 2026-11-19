package server.service.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import common.domain.Organization;
import common.domain.Person;
import common.domain.PersonTemplate;
import common.utils.validators.OrganizationCollectionValidator;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.repository.APersonRepository;
import server.service.token.Token;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import common.utils.validators.PersonCollectionValidator;

@ApplicationScoped
public class PersonCollectionService {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Person> collection = new ArrayList<>();

    @Inject
    private APersonRepository personRepository;

    @Inject
    private WorkerCollectionService workerCollectionService;

    public PersonCollectionService() {

    }

    @PostConstruct
    public void init() throws RepositoryAccessFailedException, RepositoryDataCorruptedException {
        List<Person> loaded = new ArrayList<>(personRepository.load());
        if (!PersonCollectionValidator.validate(loaded)) {
            throw new RepositoryDataCorruptedException();
        }
        collection.addAll(loaded);
    }

    public List<Person> getCollection(String order,
                                      String column,
                                      int pageNumber,
                                      int perPageNumber,
                                      int pageCount,
                                      String filter) {
        lock.readLock().lock();
        try {
            long skip = Math.max(0L, (long) (pageNumber - 1) * perPageNumber);
            long limit = (long) Math.max(0, pageCount) * perPageNumber;

            return collection.stream()
                    .filter((person) -> matchesFilter(person, column, filter))
                    .sorted((a, b) -> comparePersons(a, b, column, order))
                    .skip(skip)
                    .limit(limit)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int count(String column, String filter) {
        return (int) collection.stream()
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

    public int add(Token token, PersonTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Person person;
            try {
                person = personRepository.add(template, token);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.add(person);

            return person.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeById(Token token, int id) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (personRepository.remove(id, token)) {
                    collection.removeIf((person) -> person.getId() == id);
                    workerCollectionService.sync();

                    return true;
                }

                return false;
            } catch (RepositoryAccessFailedException | RepositoryDataCorruptedException e) {
                throw new CollectionOperationFailedException(e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean update(Token token, int id, PersonTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (personRepository.update(id, template, token)) {
                    collection.stream()
                            .filter((person) -> person.getId() == id)
                            .findFirst()
                            .ifPresent((person) -> person.update(template));

                    return true;
                }

                return false;
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}