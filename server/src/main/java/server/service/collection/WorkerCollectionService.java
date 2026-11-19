package server.service.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import common.domain.Worker;
import common.domain.WorkerTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.repository.AWorkerRepository;
import server.service.token.Token;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import common.utils.validators.WorkerCollectionValidator;

@ApplicationScoped
public class WorkerCollectionService {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Worker> collection = new ArrayList<>();

    @Inject
    private AWorkerRepository workerRepository;

    public WorkerCollectionService() {

    }

    @PostConstruct
    public void init() throws RepositoryAccessFailedException, RepositoryDataCorruptedException {
        sync();
    }

    public void sync() throws RepositoryAccessFailedException, RepositoryDataCorruptedException {
        lock.readLock().lock();
        try {
            List<Worker> loaded = new ArrayList<>(workerRepository.load());
            if (!WorkerCollectionValidator.validate(loaded)) {
                throw new RepositoryDataCorruptedException();
            }

            collection = loaded;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Worker> getCollection(String order,
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
                    .filter((worker) -> matchesFilter(worker, column, filter))
                    .sorted((a, b) -> compareWorkers(a, b, column, order))
                    .skip(skip)
                    .limit(limit)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int count(String column, String filter) {
        return (int) collection.stream()
                .filter((worker) -> matchesFilter(worker, column, filter))
                .count();
    }

    public List<Worker> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(collection);
        } finally {
            lock.readLock().unlock();
        }
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

    public int add(Token token, WorkerTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Worker worker;
            try {
                worker = workerRepository.add(template, token);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.add(worker);

            return worker.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeById(Token token, int id) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (workerRepository.remove(id, token)) {
                    collection.removeIf((worker) -> worker.getId() == id);

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

    public boolean update(Token token, int id, WorkerTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (workerRepository.update(id, template, token)) {
                    collection.stream()
                            .filter((worker) -> worker.getId() == id)
                            .findFirst()
                            .ifPresent((worker) -> worker.update(template));

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

    public Worker findById(int id) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(w -> w.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Transactional
    public Worker hire(Token token, int workerId, int organizationId)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Worker updated = workerRepository.hire(workerId, organizationId, token);
            if (updated == null) {
                return null;
            }

            collection.removeIf(w -> w.getId() == workerId);
            collection.add(updated);

            return updated;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Transactional
    public Worker indexSalary(Token token, int workerId, double coefficient)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Worker updated = workerRepository.indexSalary(workerId, coefficient, token);
            if (updated == null) {
                return null;
            }

            collection.removeIf(w -> w.getId() == workerId);
            collection.add(updated);

            return updated;
        } finally {
            lock.writeLock().unlock();
        }
    }
}