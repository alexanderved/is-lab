package server.repository.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import common.domain.Organization;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryDataCorruptedException;
import server.repository.Repository;

/**
 * Репозиторий, хранящий данные организаций в формате XML.
 */
public class XmlRepository extends Repository {
    private String filepath;

    /**
     * @param filepath Путь до файла, в который будут сохранены данные организаций
     */
    public XmlRepository(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public HashSet<Organization> load()
        throws RepositoryAccessDeniedException, RepositoryDataCorruptedException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            HashSet<Organization> orgs = XmlDeserializer.deserializeOrganizationCollection(reader);
            server.Main.logger.info("Коллекция прочитана из репозитория");

            return orgs;
        } catch (IOException e) {
            throw new RepositoryAccessDeniedException(e);
        }
    }

    @Override
    public void save(HashSet<Organization> collection)
        throws RepositoryAccessDeniedException
    {
        try (PrintWriter writer = new PrintWriter(filepath);) {
            XmlSerializer.serializeOrganizationCollection(collection, writer);

            server.Main.logger.info("Коллекция сохранена в репозиторий");
        } catch (IOException e) {
            throw new RepositoryAccessDeniedException(e);
        }
    }
}
