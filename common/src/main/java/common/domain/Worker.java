package common.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import common.utils.Validatable;
import common.utils.validators.WorkerValidator;

/**
 * Класс работника.
 */
public final class Worker implements Validatable, Comparable<Worker>, Serializable {
    private final int id; // Значение поля должно быть больше 0,
                          // Значение этого поля должно быть уникальным,
                          // Значение этого поля должно генерироваться автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private final Date creationDate; // Поле не может быть null,
                                     // Значение этого поля должно генерироваться автоматически
    private Organization organization; // Поле может быть null
    private int salary; // Значение поля должно быть больше 0
    private Long rating; // Поле может быть null, Значение поля должно быть больше 0
    private Position position; // Поле может быть null
    private Status status; // Поле может быть null
    private Person person; // Поле не может быть null

    private final int userId;

    /**
     * Конструктор, в котором датой создания устанавливается текущий момент времени.
     * @param id ID работника
     * @param userId ID пользователя, добавившего работника
     * @param template Шаблон работника
     */
    public Worker(int id, int userId, WorkerTemplate template) {
        this(id, new Date(), userId, template);
    }

    /**
     * @param id ID работника
     * @param creationDate Дата создания работника
     * @param userId ID пользователя, добавившего работника
     * @param template Шаблон работника
     */
    public Worker(int id, Date creationDate, int userId, WorkerTemplate template) {
        if (id <= 0) {
            throw new IllegalArgumentException("Поле `id` класса `Worker`"
                    + " должно быть больше 0");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("Поле `creationDate` класса `Worker`"
                    + " не должно быть null");
        }
        if (template == null) {
            throw new IllegalArgumentException("Шаблон `WorkerTemplate` не может быть null");
        }

        this.id = id;
        this.creationDate = creationDate;
        this.userId = userId;
        name = template.getName();
        coordinates = template.getCoordinates();
        organization = template.getOrganization();
        salary = template.getSalary();
        rating = template.getRating();
        position = template.getPosition();
        status = template.getStatus();
        person = template.getPerson();
    }

    /**
     * Обновляет данные работника.
     * @param template Шаблон новых данных
     */
    public void update(WorkerTemplate template) {
        name = template.getName();
        coordinates = template.getCoordinates();
        organization = template.getOrganization();
        salary = template.getSalary();
        rating = template.getRating();
        position = template.getPosition();
        status = template.getStatus();
        person = template.getPerson();
    }

    /** @return ID работника */
    public int getId() { return id; }

    /** @return имя */
    public String getName() { return name; }

    /** @return координаты */
    public Coordinates getCoordinates() { return coordinates; }

    /** @return дата создания */
    public Date getCreationDate() { return creationDate; }

    /** @return организация или null */
    public Organization getOrganization() { return organization; }

    /** @return зарплата */
    public int getSalary() { return salary; }

    /** @return рейтинг или null */
    public Long getRating() { return rating; }

    /** @return должность или null */
    public Position getPosition() { return position; }

    /** @return статус или null */
    public Status getStatus() { return status; }

    /** @return человек */
    public Person getPerson() { return person; }

    /** @return ID пользователя, добавившего работника */
    public int getUserID() { return userId; }

    @Override
    public boolean isValid() {
        return WorkerValidator.validate(this);
    }

    @Override
    public int compareTo(Worker other) {
        return Integer.compare(getSalary(), other.getSalary());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Worker worker = (Worker) o;

        if (organization == null && worker.organization != null) {
            return false;
        }
        if (rating == null && worker.rating != null) {
            return false;
        }
        if (position == null && worker.position != null) {
            return false;
        }
        if (status == null && worker.status != null) {
            return false;
        }

        return id == worker.id
                && userId == worker.userId
                && salary == worker.salary
                && name.equals(worker.name)
                && coordinates.equals(worker.coordinates)
                && creationDate.equals(worker.creationDate)
                && organization.equals(worker.organization)
                && rating.equals(worker.rating)
                && position.equals(worker.position)
                && status.equals(worker.status)
                && person.equals(worker.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, coordinates, creationDate, organization,
                salary, rating, position, status, person);
    }

    @Override
    public String toString() {
        String s = "Работник (ID: " + id + ", Дата добавления: " + creationDate + ")";
        s += "\n\tИмя: " + name;
        s += "\n\tКоординаты: " + coordinates;
        if (organization != null) {
            s += "\n\tОрганизация: " + organization;
        }
        s += "\n\tЗарплата: " + salary;
        if (rating != null) {
            s += "\n\tРейтинг: " + rating;
        }
        if (position != null) {
            s += "\n\tДолжность: " + position;
        }
        if (status != null) {
            s += "\n\tСтатус: " + status;
        }
        s += "\n\tЧеловек: " + person;

        return s;
    }
}