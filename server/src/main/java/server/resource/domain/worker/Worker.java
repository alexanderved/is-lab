package server.resource.domain.worker;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Coordinates;
import server.resource.domain.organization.Organization;
import server.resource.domain.person.Person;
import server.resource.validators.WorkerValidator;

public final class Worker implements Serializable {
    private final int id; // Значение поля должно быть больше 0,
                          // Значение этого поля должно быть уникальным,
                          // Значение этого поля должно генерироваться автоматически
    private final String name; // Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; // Поле не может быть null
    private final Date creationDate; // Поле не может быть null,
                                     // Значение этого поля должно генерироваться автоматически
    private final Organization organization; // Поле может быть null
    private final int salary; // Значение поля должно быть больше 0
    private final Long rating; // Поле может быть null, Значение поля должно быть больше 0
    private final Position position; // Поле может быть null
    private final Status status; // Поле может быть null
    private final Person person; // Поле не может быть null

    private final int userId;

    public Worker(Builder builder) throws InvalidFieldFormatException {
        try {
            id = builder.id;
            name = builder.name;
            coordinates = builder.coordinates;
            creationDate = builder.creationDate;
            organization = builder.organization;
            salary = builder.salary;
            rating = builder.rating;
            position = builder.position;
            status = builder.status;
            person = builder.person;
            userId = builder.userId;
        } catch (NullPointerException e) {
            throw new InvalidFieldFormatException(e);
        }

        if (!WorkerValidator.isValidInstance(this)) {
            throw new InvalidFieldFormatException();
        }
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public Coordinates getCoordinates() { return coordinates; }

    public Date getCreationDate() { return creationDate; }

    public Organization getOrganization() { return organization; }

    public int getSalary() { return salary; }

    public Long getRating() { return rating; }

    public Position getPosition() { return position; }

    public Status getStatus() { return status; }

    public Person getPerson() { return person; }

    public int getUserID() { return userId; }

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

    public static final class Builder {
        private Integer id;
        private String name;
        private Coordinates coordinates;
        private Date creationDate;
        private Organization organization;
        private Integer salary;
        private Long rating;
        private Position position;
        private Status status;
        private Person person;
        private Integer userId;

        public Builder setId(int id) {
            this.id = id;

            return this;
        }
        
        public Builder setName(String name) throws InvalidFieldFormatException {
            this.name = name;

            return this;
        }

        public Builder setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;

            return this;
        }

        public Builder setCreationDate(Date creationDate) {
            this.creationDate = creationDate;

            return this;
        }

        public Builder setOrganization(Organization organization) {
            this.organization = organization;

            return this;
        }

        public Builder setSalary(int salary) {
            this.salary = salary;

            return this;
        }

        public Builder setRating(Long rating) {
            this.rating = rating;

            return this;
        }

        public Builder setPosition(Position position) {
            this.position = position;

            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;

            return this;
        }

        public Builder setPerson(Person person) {
            this.person = person;

            return this;
        }

        public Builder setUserId(int userId) {
            this.userId = userId;

            return this;
        }

        public Worker build() throws InvalidFieldFormatException {
            return new Worker(this);
        }
    }
}