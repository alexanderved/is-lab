package common.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.PersonTemplateValidator;

/**
 * Шаблон человека, представляющий собой набор данных этого человека.
 */
public final class PersonTemplate implements Validatable, Comparable<Person>, Serializable {
    private Color eyeColor;
    private Color hairColor;
    private Location location;
    private Date birthday;
    private int weight;
    private Country nationality;

    private PersonTemplate() {

    }

    private PersonTemplate(Builder builder) {
        eyeColor = builder.eyeColor;
        hairColor = builder.hairColor;
        location = builder.location;
        birthday = builder.birthday;
        weight = builder.weight;
        nationality = builder.nationality;
    }

    /** @return цвет глаз */
    public Color getEyeColor() { return eyeColor; }

    /** @return цвет волос */
    public Color getHairColor() { return hairColor; }

    /** @return местоположение */
    public Location getLocation() { return location; }

    /** @return дата рождения или null */
    public Date getBirthday() { return birthday; }

    /** @return вес */
    public int getWeight() { return weight; }

    /** @return национальность или null */
    public Country getNationality() { return nationality; }

    @Override
    public boolean isValid() {
        return PersonTemplateValidator.validate(this);
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(getWeight(), other.getWeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonTemplate person = (PersonTemplate) o;

        if (birthday == null && person.birthday != null) {
            return false;
        }
        if (nationality == null && person.nationality != null) {
            return false;
        }

        return weight == person.weight
                && eyeColor.equals(person.eyeColor)
                && hairColor.equals(person.hairColor)
                && location.equals(person.location)
                && birthday.equals(person.birthday)
                && nationality.equals(person.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eyeColor, hairColor, location, birthday, weight, nationality);
    }

    @Override
    public String toString() {
        String s = "Шаблон Человека";
        s += "\n\tЦвет глаз: " + eyeColor;
        s += "\n\tЦвет волос: " + hairColor;
        s += "\n\tМестоположение: " + location;
        if (birthday != null) {
            s += "\n\tДата рождения: " + birthday;
        }
        s += "\n\tВес: " + weight;
        if (nationality != null) {
            s += "\n\tНациональность: " + nationality;
        }

        return s;
    }

    /**
     * Строитель экземпляров класса {@code PersonTemplate}.
     */
    public static final class Builder {
        private Color eyeColor;
        private Color hairColor;
        private Location location;
        private Date birthday;
        private Integer weight;
        private Country nationality;

        /**
         * Устанавливает цвет глаз.
         * @param eyeColor Цвет глаз
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setEyeColor(Color eyeColor) throws InvalidFieldFormatException {
            if (!PersonTemplateValidator.validateEyeColor(eyeColor)) {
                throw new InvalidFieldFormatException("eyeColor");
            }
            this.eyeColor = eyeColor;

            return this;
        }

        /**
         * Устанавливает цвет волос.
         * @param hairColor Цвет волос
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setHairColor(Color hairColor) throws InvalidFieldFormatException {
            if (!PersonTemplateValidator.validateHairColor(hairColor)) {
                throw new InvalidFieldFormatException("hairColor");
            }
            this.hairColor = hairColor;

            return this;
        }

        /**
         * Устанавливает местоположение.
         * @param location Местоположение
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setLocation(Location location) throws InvalidFieldFormatException {
            if (!PersonTemplateValidator.validateLocation(location)) {
                throw new InvalidFieldFormatException("location");
            }
            this.location = location;

            return this;
        }

        /**
         * Устанавливает дату рождения.
         * @param birthday Дата рождения
         * @return Строитель экземпляров класса
         */
        public Builder setBirthday(Date birthday) {
            this.birthday = birthday;

            return this;
        }

        /**
         * Устанавливает вес.
         * @param weight Вес
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setWeight(int weight) throws InvalidFieldFormatException {
            if (!PersonTemplateValidator.validateWeight(weight)) {
                throw new InvalidFieldFormatException("weight");
            }
            this.weight = weight;

            return this;
        }

        /**
         * Устанавливает национальность.
         * @param nationality Национальность
         * @return Строитель экземпляров класса
         */
        public Builder setNationality(Country nationality) {
            this.nationality = nationality;

            return this;
        }

        /**
         * Проверяет, установлены ли все обязательные поля.
         * @return {@code true}, если все обязательные поля установлены
         */
        public boolean isComplete() {
            return eyeColor != null && hairColor != null
                    && location != null && weight != null;
        }

        /**
         * Создает экземпляр класса {@code PersonTemplate}.
         * @return экземпляр класса {@code PersonTemplate}
         * @throws IllegalStateException если не все обязательные поля установлены
         */
        public PersonTemplate build() {
            if (!isComplete()) {
                throw new IllegalStateException(
                        "Метод `build` класса `PersonTemplate.Builder` не может быть"
                                + " вызван пока не будут установлены все обязательные поля");
            }

            return new PersonTemplate(this);
        }
    }
}