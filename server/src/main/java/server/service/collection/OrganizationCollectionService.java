package server.service.collection;

import java.util.List;

import server.repository.OrganizationRepository;
import server.resource.domain.organization.Organization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.resource.dto.OrganizationDTO;
import server.service.token.Token;

@ApplicationScoped
public class OrganizationCollectionService {
    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private WorkerCollectionService workerCollectionService;

    public OrganizationCollectionService() {

    }

    @Transactional
    public List<Organization> getCollection(String order,
                                            String column,
                                            int pageNumber,
                                            int perPageNumber,
                                            int pageCount,
                                            String filter) {
        List<Organization> collection = organizationRepository.load();

        long skip = Math.max(0L, (long) (pageNumber - 1) * perPageNumber);
        long limit = (long) Math.max(0, pageCount) * perPageNumber;

        return collection.stream()
                .filter((org) -> matchesFilter(org, column, filter))
                .sorted((a, b) -> compareOrganizations(a, b, column, order))
                .skip(skip)
                .limit(limit)
                .toList();
    }

    @Transactional
    public int count(String column, String filter) {
        return (int) organizationRepository.load()
                .stream()
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

    @Transactional
    public int add(Token token, OrganizationDTO dto) {
        return organizationRepository.add(dto, token).getId();
    }

    @Transactional
    public boolean removeById(Token token, int id) {
        return organizationRepository.remove(id, token);
    }

    @Transactional
    public boolean update(Token token, int id, OrganizationDTO dto) {
        return organizationRepository.update(id, dto, token);
    }
}