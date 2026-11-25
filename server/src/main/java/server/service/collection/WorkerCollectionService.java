package server.service.collection;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import server.repository.WorkerRepository;
import server.resource.domain.worker.Worker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.resource.dto.WorkerDTO;
import server.service.token.Token;
import server.utils.TransactionManager;

@ApplicationScoped
public class WorkerCollectionService {
    @Inject
    private WorkerRepository workerRepository;

    @Inject
    private TransactionManager transactionManager;

    public WorkerCollectionService() {

    }

    @Transactional
    public List<Worker> getCollection(String order,
                                      String column,
                                      int pageNumber,
                                      int perPageNumber,
                                      int pageCount,
                                      String filter) {
        List<Worker> collection = workerRepository.load();

        long skip = Math.max(0L, (long) (pageNumber - 1) * perPageNumber);
        long limit = (long) Math.max(0, pageCount) * perPageNumber;

        return collection.stream()
                .filter((worker) -> matchesFilter(worker, column, filter))
                .sorted((a, b) -> compareWorkers(a, b, column, order))
                .skip(skip)
                .limit(limit)
                .toList();
    }

    @Transactional
    public int count(String column, String filter) {
        return (int) workerRepository.load()
                .stream()
                .filter((worker) -> matchesFilter(worker, column, filter))
                .count();
    }

    @Transactional
    public List<Worker> getAll() {
        return workerRepository.load();
    }

    private static boolean matchesFilter(Worker worker, String column, String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        String value = switch (column) {
            case "id" -> String.valueOf(worker.getId());
            case "name" -> worker.getName();
            case "coordinates" -> worker.getCoordinates() == null
                    ? null
                    : worker.getCoordinates().toString();
            case "creationDate" -> String.valueOf(worker.getCreationDate());
            case "organization" -> worker.getOrganization() == null
                    ? null
                    : String.valueOf(worker.getOrganization().getId());
            case "salary" -> String.valueOf(worker.getSalary());
            case "rating" -> worker.getRating() == null ? null : String.valueOf(worker.getRating());
            case "position" -> worker.getPosition() == null ? null : worker.getPosition().toString();
            case "status" -> worker.getStatus() == null ? null : worker.getStatus().toString();
            case "person" -> worker.getPerson() == null
                    ? null
                    : String.valueOf(worker.getPerson().getId());
            default -> null;
        };

        return value != null && value.contains(filter);
    }

    private static int compareWorkers(Worker a, Worker b, String field, String order) {
        int result = switch (field) {
            case "id" -> Integer.compare(a.getId(), b.getId());
            case "name" -> a.getName().compareTo(b.getName());
            case "coordinates" -> {
                int cx = Integer.compare(a.getCoordinates().getX(), b.getCoordinates().getX());
                yield cx != 0 ? cx
                        : Float.compare(a.getCoordinates().getY(), b.getCoordinates().getY());
            }
            case "creationDate" -> a.getCreationDate().compareTo(b.getCreationDate());
            case "organization" -> compareNullable(
                    a.getOrganization() == null ? null : a.getOrganization().getId(),
                    b.getOrganization() == null ? null : b.getOrganization().getId());
            case "salary" -> Integer.compare(a.getSalary(), b.getSalary());
            case "rating" -> compareNullable(a.getRating(), b.getRating());
            case "position" -> compareNullable(a.getPosition(), b.getPosition());
            case "status" -> compareNullable(a.getStatus(), b.getStatus());
            case "person" -> Integer.compare(a.getPerson().getId(), b.getPerson().getId());
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

    public int add(Token token, WorkerDTO dto) {
        return transactionManager.run(() -> this.doAdd(token, dto));
    }

    @Transactional
    public int doAdd(Token token, WorkerDTO dto) {
        transactionManager.makeSerializable();
        return workerRepository.add(dto, token).getId();
    }

    @Transactional
    public boolean removeById(Token token, int id) {
        return workerRepository.remove(id, token);
    }

    public boolean update(Token token, int id, WorkerDTO dto) {
        return transactionManager.run(() -> this.doUpdate(token, id, dto));
    }

    @Transactional
    public boolean doUpdate(Token token, int id, WorkerDTO dto) {
        transactionManager.makeSerializable();
        return workerRepository.update(id, dto, token);
    }

    @Transactional
    public Worker hire(Token token, int workerId, int organizationId) {
        return workerRepository.hire(workerId, organizationId, token);
    }

    @Transactional
    public Worker indexSalary(Token token, int workerId, double coefficient) {
        return workerRepository.indexSalary(workerId, coefficient, token);
    }
}