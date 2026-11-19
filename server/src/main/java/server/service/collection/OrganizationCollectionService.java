package server.service.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jakarta.annotation.PostConstruct;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.repository.AOrganizationRepository;
import server.service.token.Token;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import common.utils.validators.OrganizationCollectionValidator;

@ApplicationScoped
public class OrganizationCollectionService {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Organization> collection = new ArrayList<>();

    @Inject
    private AOrganizationRepository organizationRepository;

    @Inject
    private WorkerCollectionService workerCollectionService;

    public OrganizationCollectionService() {

    }

    @PostConstruct
    public void init() throws RepositoryAccessFailedException, RepositoryDataCorruptedException {
        List<Organization> loaded = new ArrayList<>(organizationRepository.load());
        if (!OrganizationCollectionValidator.validate(loaded)) {
            throw new RepositoryDataCorruptedException();
        }
        collection.addAll(loaded);
    }

    public List<Organization> getCollection(String order,
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
                    .filter((org) -> matchesFilter(org, column, filter))
                    .sorted((a, b) -> compareOrganizations(a, b, column, order))
                    .skip(skip)
                    .limit(limit)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int count(String column, String filter) {
        return (int) collection.stream()
                .filter((org) -> matchesFilter(org, column, filter))
                .count();
    }

    private static boolean matchesFilter(Organization org, String column, String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        String value = switch (column) {
            case "id" -> String.valueOf(org.getId());
            case "annualTurnover" -> String.valueOf(org.getAnnualTurnover());
            case "fullName" -> org.getFullName();
            case "employeesCount" -> String.valueOf(org.getEmployeesCount());
            case "rating" -> org.getRating() == null ? null : String.valueOf(org.getRating());
            case "type" -> org.getType().toString();
            case "officialAddress" -> org.getOfficialAddress() == null
                    ? null : org.getOfficialAddress().toString();
            case "postalAddress" -> org.getPostalAddress() == null
                    ? null : org.getPostalAddress().toString();
            default -> null;
        };

        return value != null && value.contains(filter);
    }

    private static int compareOrganizations(Organization a, Organization b, String field, String order) {
        int result = switch (field) {
            case "id" -> Integer.compare(a.getId(), b.getId());
            case "annualTurnover" -> Long.compare(a.getAnnualTurnover(), b.getAnnualTurnover());
            case "fullName" -> compareNullable(a.getFullName(), b.getFullName());
            case "employeesCount" -> Long.compare(a.getEmployeesCount(), b.getEmployeesCount());
            case "rating" -> compareNullable(a.getRating(), b.getRating());
            case "type" -> a.getType().compareTo(b.getType());
            case "officialAddress" -> compareNullable(
                    a.getOfficialAddress() == null ? null : a.getOfficialAddress().toString(),
                    b.getOfficialAddress() == null ? null : b.getOfficialAddress().toString());
            case "postalAddress" -> compareNullable(
                    a.getPostalAddress() == null ? null : a.getPostalAddress().toString(),
                    b.getPostalAddress() == null ? null : b.getPostalAddress().toString());
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

    public int add(Token token, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Organization org;
            try {
                org = organizationRepository.add(template, token);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.add(org);

            return org.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeById(Token token, int id) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (organizationRepository.remove(id, token)) {
                    collection.removeIf((org) -> org.getId() == id);
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

    public boolean update(Token token, int id, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (organizationRepository.update(id, template, token)) {
                    collection.stream()
                            .filter((org) -> org.getId() == id)
                            .findFirst()
                            .ifPresent((org) -> org.update(template));

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