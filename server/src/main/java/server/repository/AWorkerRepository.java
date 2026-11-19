package server.repository;

import common.domain.Worker;
import common.domain.WorkerTemplate;
import server.service.token.Token;
import server.exceptions.RepositoryAccessFailedException;

import java.util.List;

public abstract class AWorkerRepository {
    public abstract List<Worker> load() throws RepositoryAccessFailedException;
    public abstract Worker add(WorkerTemplate template, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean remove(int id, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean update(int id, WorkerTemplate template, Token token)
            throws RepositoryAccessFailedException;
    public abstract Worker hire(int workerId, int organizationId, Token token);
    public abstract Worker indexSalary(int workerId, double coefficient, Token token);
}
