package server.application;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import common.domain.User;
import common.exceptions.UserNotFoundException;
import server.exceptions.*;
import server.logger.Logger;
import server.repository.Repository;
import common.utils.validators.OrganizationCollectionValidator;

/**
 * Класс, оперирующий коллекцией организаций.
 */
public class CollectionManager {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final HashSet<Organization> collection;
    private final IDGenerator idGen;
    private final Repository repo;
    private final UserManager userManager;
    private final ZonedDateTime initDate;
    private ZonedDateTime lastModDate;

    /**
     * @param idGen Генератор ID для добавляемых в коллекцию организаций
     * @param repo Репозиторий организаций
     * @param userManager Класс, управляющий данными пользователей
     * @throws RepositoryAccessDeniedException если не удается получить
     *                                         доступ к репозиторию
     * @throws RepositoryDataCorruptedException если данные в репозитории не соответствуют
     *                                          ожидаемому формату
     * @see IDGenerator
     * @see Repository
     */
    public CollectionManager(IDGenerator idGen, Repository repo, UserManager userManager)
        throws RepositoryAccessDeniedException, RepositoryDataCorruptedException
    {
        if (idGen == null) {
            throw new IllegalArgumentException(
                    "`idGen` в классе `CollectionManager` не должно быть null");
        }
        this.idGen = idGen;

        if (repo == null) {
            throw new IllegalArgumentException(
                    "`repo` в классе `CollectionManager` не должно быть null");
        }
        this.repo = repo;

        if (userManager == null) {
            throw new IllegalArgumentException(
                    "`userManager` в классе `CollectionManager` не должно быть null");
        }
        this.userManager = userManager;

        collection = loadCollection(repo, idGen);
        initDate = ZonedDateTime.now();
        lastModDate = initDate;
    }

    private static HashSet<Organization> loadCollection(Repository repo, IDGenerator idGen)
        throws RepositoryAccessDeniedException, RepositoryDataCorruptedException
    {
        HashSet<Organization> collection = repo.load();
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
     * @param user     Пользователь, выполняющий операцию
     * @param template Шаблон новой организации
     * @return ID новой организации
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     * @see OrganizationTemplate
     */
    public int add(User user, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            int userId;
            try {
                userId = userManager.getUserID(user);
            } catch (UserNotFoundException | UserDBAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            int newId = idGen.generateID();
            Organization org = new Organization(newId, userId, template);
            try {
                repo.add(org);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.add(org);
            updateLastModDate();

            return newId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаляет организацию из коллекции по ее ID.
     *
     * @param user Пользователь, выполняющий операцию
     * @param id   ID организации, которую нужно удалить
     * @return {@code true}, если элемент с данным ID присутствовал в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public boolean removeById(User user, int id) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            int userId;
            try {
                userId = userManager.getUserID(user);
            } catch (UserNotFoundException | UserDBAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            Optional<Organization> removableOrg = collection.stream()
                    .filter((org) -> org.getId() == id)
                    .findFirst();
            if (removableOrg.isEmpty()) {
                return false;
            }

            if (removableOrg.get().getUserID() != userId) {
                return false;
            }

            try {
                repo.remove(userId, id);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            collection.remove(removableOrg.get());
            updateLastModDate();

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаляет из коллекции все организации, которые меньше,
     * чем организация, созданная по данному шаблону.
     *
     * @param user     Пользователь, выполняющий операцию
     * @param template Шаблон организации, с которой происходит
     *                 сравнение элементов коллекции
     * @see OrganizationTemplate
     */
    public void removeLower(User user, OrganizationTemplate template) {
        lock.writeLock().lock();
        try {
            List<Integer> removedIds = collection.stream()
                    .filter((org) -> template.compareTo(org) > 0)
                    .map(Organization::getId)
                    .toList();

            removedIds.forEach((id) -> {
                try {
                    removeById(user, id);
                } catch (CollectionOperationFailedException e) {
                    Logger.getGlobal().warn("Не удалось удалить организацию с id = " + id, e);
                }
            });
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Обновляет информацию организации с данным ID с использованием данного шаблона.
     *
     * @param user     Пользователь, выполняющий операцию
     * @param id       ID организации, которую нужно обновить
     * @param template Шаблон новых данных организации
     * @return {@code true}, если элемент с данным ID присутствует в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public boolean update(User user, int id, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            int userId;
            try {
                userId = userManager.getUserID(user);
            } catch (UserNotFoundException | UserDBAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            Optional<Organization> updatableOrg = collection.stream()
                    .filter((org) -> org.getId() == id)
                    .findFirst();
            if (updatableOrg.isEmpty()) {
                return false;
            }

            if (updatableOrg.get().getUserID() != userId) {
                return false;
            }

            try {
                repo.update(userId, id, template);
            } catch (RepositoryAccessFailedException e) {
                throw new CollectionOperationFailedException(e);
            }

            updatableOrg.get().update(template);
            updateLastModDate();

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Очищает коллекцию.
     *
     * @param user Пользователь, выполняющий операцию
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public void clear(User user) throws CollectionOperationFailedException {
        lock.writeLock().lock();
        try {
            int userId = userManager.getUserID(user);
            repo.clear(userId);

            collection.removeIf((org) -> org.getUserID() == userId);
            updateLastModDate();
        } catch (UserNotFoundException | UserDBAccessFailedException | RepositoryAccessFailedException e) {
            throw new CollectionOperationFailedException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return наибольший элемент коллекции
     * @see Organization
     */
    public Organization max() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .max(Organization::compareTo)
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return наименьший элемент коллекции
     * @see Organization
     */
    public Organization min() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .min(Organization::compareTo)
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return среднее арифметическое количеств сотрудников каждой из организаций
     */
    public int averageOfEmployeesCount() {
        lock.readLock().lock();
        try {
            int nbOrgs = getCollectionSize();
            if (nbOrgs == 0) {
                return 0;
            }

            return collection.stream()
                    .map(Organization::getEmployeesCount)
                    .reduce(0, Integer::sum) / nbOrgs;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return список годовых оборотов всех организаций
     */
    public List<Float> getAnnualTurnovers() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .map(Organization::getAnnualTurnover)
                    .filter(Objects::nonNull)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }
}