package server.repository;

import java.util.HashSet;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import server.logger.Logger;

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
    public abstract void add(Organization org) throws RepositoryAccessFailedException;
    public abstract void remove(int id) throws RepositoryAccessFailedException;
    public abstract void clear() throws RepositoryAccessFailedException;
    public abstract void update(int id, OrganizationTemplate template) throws RepositoryAccessFailedException;
}