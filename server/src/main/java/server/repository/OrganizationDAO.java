package server.repository;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import server.service.token.Token;
import server.exceptions.RepositoryAccessFailedException;

import java.util.List;

public abstract class OrganizationDAO {
    public abstract List<Organization> load() throws RepositoryAccessFailedException;
    public abstract Organization add(OrganizationTemplate template, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean remove(int id, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean update(int id, OrganizationTemplate template, Token token)
            throws RepositoryAccessFailedException;
}
