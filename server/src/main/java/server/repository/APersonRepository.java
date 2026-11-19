package server.repository;

import common.domain.Person;
import common.domain.PersonTemplate;
import server.service.token.Token;
import server.exceptions.RepositoryAccessFailedException;

import java.util.List;

public abstract class APersonRepository {
    public abstract List<Person> load() throws RepositoryAccessFailedException;
    public abstract Person add(PersonTemplate template, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean remove(int id, Token token)
            throws RepositoryAccessFailedException;
    public abstract boolean update(int id, PersonTemplate template, Token token)
            throws RepositoryAccessFailedException;
}
