package server.application;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.RepositoryDataCorruptedException;
import server.logger.Logger;
import server.repository.Repository;
import common.utils.validators.OrganizationCollectionValidator;

/**
 * Класс, оперирующий коллекцией организаций.
 */
// TODO: Replace `synchronized` with another method from the task
public class CollectionManager {
    private final HashSet<Organization> collection;
    private final IDGenerator idGen;
    private final Repository repo;
    private final ZonedDateTime initDate;
    private ZonedDateTime lastModDate;

    /**
     * @param idGen Генератор ID для добавляемых в коллекцию организаций
     * @param repo Репозиторий организаций
     * @throws RepositoryAccessDeniedException если не удается получить
     *                                         доступ к репозиторию
     * @throws RepositoryDataCorruptedException если данные в репозитории не соответствуют
     *                                          ожидаемому формату
     * @see IDGenerator
     * @see Repository
     */
    public CollectionManager(IDGenerator idGen, Repository repo)
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
    public synchronized int getCollectionSize() {
        return collection.size();
    }

    /**
     * @return время инициализации управляемой коллекции
     */
    public synchronized ZonedDateTime getInitDate() {
        return initDate;
    }

    /**
     * @return время модификации управляемой коллекции
     */
    public synchronized ZonedDateTime getLastModDate() {
        return lastModDate;
    }

    private synchronized void updateLastModDate() {
        lastModDate = ZonedDateTime.now();
    }

    /**
     * @return тип управляемой коллекции
     */
    public synchronized String getCollectionType() {
        return collection.getClass().getName();
    }

    /**
     * @return коллекцию, элементы которой отсортированы по имени
     */
    public synchronized List<Organization> getSortedCollection() {
        return collection.stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .toList();
    }

    /**
     * Добавляет в коллекцию новую организацию, созданную по шаблону.
     * 
     * @param template Шаблон новой организации
     * @return ID новой организации
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     * @see OrganizationTemplate
     */
    public synchronized int add(OrganizationTemplate template)
            throws CollectionOperationFailedException {
        int newId = idGen.generateID();
        Organization org = new Organization(newId, template);
        try {
            repo.add(org);
        } catch (RepositoryAccessFailedException e) {
            throw new CollectionOperationFailedException(e);
        }

        collection.add(org);
        updateLastModDate();

        return newId;
    }

    /**
     * Удаляет организацию из коллекции по ее ID.
     * @param id ID организации, которую нужно удалить
     * @return {@code true}, если элемент с данным ID присутствовал в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public synchronized boolean removeById(int id) throws CollectionOperationFailedException {
        try {
            repo.remove(id);
        } catch (RepositoryAccessFailedException e) {
            throw new CollectionOperationFailedException(e);
        }

        if (collection.removeIf((org) -> org.getId() == id)) {
            updateLastModDate();

            return true;
        }

        return false;
    }

    /**
     * Удаляет из коллекции все организации, которые меньше,
     * чем организация, созданная по данному шаблону.
     * @param template Шаблон организации, с которой происходит
     *                 сравнение элементов коллекции
     * @see OrganizationTemplate
     */
    public synchronized void removeLower(OrganizationTemplate template) {
        List<Integer> removedIds = collection.stream()
                .filter((org) -> template.compareTo(org) > 0)
                .map(Organization::getId)
                .toList();

        removedIds.forEach((id) -> {
                    try {
                        removeById(id);
                    } catch (CollectionOperationFailedException e) {
                        Logger.getGlobal().warn("Не удалось удалить организацию с id = " + id, e);
                    }
                });
    }

    /**
     * Обновляет информацию организации с данным ID с использованием данного шаблона.
     * @param id ID организации, которую нужно обновить
     * @param template Шаблон новых данных организации
     * @return {@code true}, если элемент с данным ID присутствует в коллекции
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public synchronized boolean update(int id, OrganizationTemplate template)
            throws CollectionOperationFailedException {
        try {
            repo.update(id, template);
        } catch (RepositoryAccessFailedException e) {
            throw new CollectionOperationFailedException(e);
        }

        Optional<Organization> org = collection.stream()
            .filter((o) -> o.getId() == id)
            .findFirst();

        if (org.isEmpty()) {
            return false;
        }
        org.get().update(template);
        updateLastModDate();

        return true;
    }

    /**
     * Очищает коллекцию.
     *
     * @throws CollectionOperationFailedException если не удалось изменить данные в репозитории
     */
    public synchronized void clear() throws CollectionOperationFailedException {
        try {
            repo.clear();
        } catch (RepositoryAccessFailedException e) {
            throw new CollectionOperationFailedException(e);
        }

        collection.clear();
        updateLastModDate();
    }

    /**
     * @return наибольший элемент коллекции
     * @see Organization
     */
    public synchronized Organization max() {
        return collection.stream()
            .max(Organization::compareTo)
            .orElse(null);
    }

    /**
     * @return наименьший элемент коллекции
     * @see Organization
     */
    public synchronized Organization min() {
        return collection.stream()
            .min(Organization::compareTo)
            .orElse(null);
    }

    /**
     * @return среднее арифметическое количеств сотрудников каждой из организаций
     */
    public synchronized int averageOfEmployeesCount() {
        int nbOrgs = getCollectionSize();
        if (nbOrgs == 0) {
            return 0;
        }

        return collection.stream()
            .map(Organization::getEmployeesCount)
            .reduce(0, Integer::sum) / nbOrgs;
    }

    /**
     * @return список годовых оборотов всех организаций
     */
    public synchronized List<Float> getAnnualTurnovers() {
        return collection.stream()
            .map(Organization::getAnnualTurnover)
            .filter(Objects::nonNull)
            .toList();
    }
}