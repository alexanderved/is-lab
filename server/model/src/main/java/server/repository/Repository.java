package server.repository;

import java.util.HashSet;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryAccessFailedException;
import common.exceptions.UserNotFoundException;

/**
 * Репозиторий данных организаций.
 */
public abstract class Repository {
    /**
     * Загружает данные организаций из репозитория.
     * 
     * @return данные организаций
     * @throws RepositoryAccessDeniedException  если не удается получить доступ к
     *                                          репозиторию
     */
    public abstract HashSet<Organization> load() throws RepositoryAccessDeniedException;
    public abstract HashedUser getHashedUser(String name) throws RepositoryAccessFailedException, UserNotFoundException;
    public abstract void registerUser(String name, String passwordHash, String salt)
            throws RepositoryAccessFailedException;
    public abstract void add(Organization org) throws RepositoryAccessFailedException;
    public abstract void remove(int userId, int id) throws RepositoryAccessFailedException;
    public abstract void clear(int userId) throws RepositoryAccessFailedException;
    public abstract void update(int userId, int id, OrganizationTemplate template) throws RepositoryAccessFailedException;
}