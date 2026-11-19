package server.service.operations;

import common.domain.Person;
import common.domain.Worker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.exceptions.CollectionOperationFailedException;
import server.service.collection.WorkerCollectionService;
import server.service.token.Token;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class SpecialOperationsService {
    @Inject
    private WorkerCollectionService workerCollection;

    public long calculateRatingSum() {
        return workerCollection.getAll().stream()
                .map(Worker::getRating)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    public double calculateRatingAverage() {
        return workerCollection.getAll().stream()
                .map(Worker::getRating)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    public List<Person> getUniquePersons() {
        return workerCollection.getAll().stream()
                .map(Worker::getPerson)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public Worker hire(Token token, int workerId, int organizationId)
            throws CollectionOperationFailedException {
        return workerCollection.hire(token, workerId, organizationId);
    }

    public Worker indexSalary(Token token, int workerId, double coefficient)
            throws CollectionOperationFailedException {
        return workerCollection.indexSalary(token, workerId, coefficient);
    }
}