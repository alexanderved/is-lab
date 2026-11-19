package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.WorkerTemplateValidator;

/**
 * Шаблон работника, представляющий собой набор данных этого работника.
 */
public final class WorkerTemplate implements Validatable, Comparable<Worker>, Serializable {
    private String name;
    private Coordinates coordinates;
    private Organization organization;
    private int salary;
    private Long rating;
    private Position position;
    private Status status;
    private Person person;

    private WorkerTemplate() {

    }

    private WorkerTemplate(Builder builder) {
        name = builder.name;
        coordinates = builder.coordinates;
        organization = builder.organization;
        salary = builder.salary;
        rating = builder.rating;
        position = builder.position;
        status = builder.status;
        person = builder.person;
    }

    /** @return имя */
    public String getName() { return name; }

    /** @return координаты */
    public Coordinates getCoordinates() { return coordinates; }

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

    @Override
    public boolean isValid() {
        return WorkerTemplateValidator.validate(this);
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

        WorkerTemplate worker = (WorkerTemplate) o;

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

        return salary == worker.salary
                && name.equals(worker.name)
                && coordinates.equals(worker.coordinates)
                && organization.equals(worker.organization)
                && rating.equals(worker.rating)
                && position.equals(worker.position)
                && status.equals(worker.status)
                && person.equals(worker.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinates, organization, salary,
                rating, position, status, person);
    }

    @Override
    public String toString() {
        String s = "Шаблон Работника";
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

    /**
     * Строитель экземпляров класса {@code WorkerTemplate}.
     */
    public static final class Builder {
        private String name;
        private Coordinates coordinates;
        private Organization organization;
        private Integer salary;
        private Long rating;
        private Position position;
        private Status status;
        private Person person;

        /**
         * Устанавливает имя работника.
         * @param name Имя работника
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setName(String name) throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validateName(name)) {
                throw new InvalidFieldFormatException("name");
            }
            this.name = name;

            return this;
        }

        /**
         * Устанавливает координаты работника.
         * @param coordinates Координаты работника
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setCoordinates(Coordinates coordinates)
                throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validateCoordinates(coordinates)) {
                throw new InvalidFieldFormatException("coordinates");
            }
            this.coordinates = coordinates;

            return this;
        }

        /**
         * Устанавливает организацию работника.
         * @param organization Организация работника
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setOrganization(Organization organization)
                throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validateOrganization(organization)) {
                throw new InvalidFieldFormatException("organization");
            }
            this.organization = organization;

            return this;
        }

        /**
         * Устанавливает зарплату работника.
         * @param salary Зарплата работника
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setSalary(int salary) throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validateSalary(salary)) {
                throw new InvalidFieldFormatException("salary");
            }
            this.salary = salary;

            return this;
        }

        /**
         * Устанавливает рейтинг работника.
         * @param rating Рейтинг работника
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setRating(Long rating) throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validateRating(rating)) {
                throw new InvalidFieldFormatException("rating");
            }
            this.rating = rating;

            return this;
        }

        /**
         * Устанавливает должность работника.
         * @param position Должность работника
         * @return Строитель экземпляров класса
         */
        public Builder setPosition(Position position) {
            this.position = position;

            return this;
        }

        /**
         * Устанавливает статус работника.
         * @param status Статус работника
         * @return Строитель экземпляров класса
         */
        public Builder setStatus(Status status) {
            this.status = status;

            return this;
        }

        /**
         * Устанавливает человека, соответствующего работнику.
         * @param person Человек
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setPerson(Person person) throws InvalidFieldFormatException {
            if (!WorkerTemplateValidator.validatePerson(person)) {
                throw new InvalidFieldFormatException("person");
            }
            this.person = person;

            return this;
        }

        /**
         * Проверяет, установлены ли все обязательные поля.
         * @return {@code true}, если все обязательные поля установлены
         */
        public boolean isComplete() {
            return name != null && coordinates != null
                    && salary != null && person != null;
        }

        /**
         * Создает экземпляр класса {@code WorkerTemplate}.
         * @return экземпляр класса {@code WorkerTemplate}
         * @throws IllegalStateException если не все обязательные поля установлены
         */
        public WorkerTemplate build() {
            if (!isComplete()) {
                throw new IllegalStateException(
                        "Метод `build` класса `WorkerTemplate.Builder` не может быть"
                                + " вызван пока не будут установлены все обязательные поля");
            }

            return new WorkerTemplate(this);
        }
    }
}