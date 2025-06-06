package server.service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import org.springframework.stereotype.Service;
import server.repository.OrganizationDAO;
import server.service.token.Token;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import server.logger.Logger;
import common.utils.validators.OrganizationCollectionValidator;

/**
 * Класс, оперирующий коллекцией организаций.
 */
@Service
public class CollectionService {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final HashSet<Organization> collection;
    private final OrganizationDAO organizationDAO;
    private final ZonedDateTime initDate;
    private ZonedDateTime lastModDate;

    /**
     * @param organizationDAO Репозиторий организаций
     * @throws RepositoryAccessFailedException если не удается получить
     *                                         доступ к репозиторию
     * @throws RepositoryDataCorruptedException если данные в репозитории не соответствуют
     *                                          ожидаемому формату
     */
    public CollectionService(OrganizationDAO organizationDAO)
            throws RepositoryAccessFailedException, RepositoryDataCorruptedException {
        if (organizationDAO == null) {
            throw new IllegalArgumentException(
                    "`organizationDAO` в классе `CollectionManager` не должно быть null");
        }
        this.organizationDAO = organizationDAO;

        collection = loadCollection(organizationDAO);
        initDate = ZonedDateTime.now();
        lastModDate = initDate;
    }

    private static HashSet<Organization> loadCollection(OrganizationDAO organizationDAO)
        throws RepositoryAccessFailedException, RepositoryDataCorruptedException
    {
        HashSet<Organization> collection = new HashSet<>(organizationDAO.load());
        if (!OrganizationCollectionValidator.validate(collection)) {
            throw new RepositoryDataCorruptedException();
        }

        Logger.getGlobal().info("Коллекция загружена в память");

        return collection;
    }

    /**
     * @return размер управляемой коллекции
     */
    public int getCollectionSize() {
        lock.readLock().lock();
        try {
            return collection.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return время инициализации управляемой коллекции
     */
    public ZonedDateTime getInitDate() {
        return initDate;
    }

    /**
     * @return время модификации управляемой коллекции
     */
    public ZonedDateTime getLastModDate() {
        lock.readLock().lock();
        try {
            return lastModDate;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void updateLastModDate() {
        lock.writeLock().lock();
        try {
            lastModDate = ZonedDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return тип управляемой коллекции
     */
    public String getCollectionType() {
        lock.readLock().lock();
        try {
            return collection.getClass().getName();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return коллекцию, элементы которой отсортированы по имени
     */
    public List<Organization> getSortedCollection() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .sorted((a, b) -> a.getName().compareTo(b.getName()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Добавляет в коллекцию новую организацию, созданную по шаблону.
     *
     * @param token    Токен пользователя, выполняющий операцию
     * @param template Шаблон новой организации
     * @return ID новой организации
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     * @see OrganizationTemplate
     */
    public int add(Token token, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            Organization org;
            try {
                org = organizationDAO.add(template, token);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.add(org);
            updateLastModDate();

            return org.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаляет организацию из коллекции по ее ID.
     *
     * @param token Токен пользователя, выполняющий операцию
     * @param id    ID организации, которую нужно удалить
     * @return {@code true}, если элемент с данным ID присутствовал в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public boolean removeById(Token token, int id) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (organizationDAO.remove(id, token)) {
                    collection.removeIf((org) -> org.getId() == id);
                    updateLastModDate();

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

    /**
     * Обновляет информацию организации с данным ID с использованием данного шаблона.
     *
     * @param token    Токен пользователя, выполняющий операцию
     * @param id       ID организации, которую нужно обновить
     * @param template Шаблон новых данных организации
     * @return {@code true}, если элемент с данным ID присутствует в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public boolean update(Token token, int id, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            try {
                if (organizationDAO.update(id, template, token)) {
                    collection.stream()
                            .filter((org) -> org.getId() == id)
                            .findFirst()
                            .get()
                            .update(template);
                    updateLastModDate();

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
}