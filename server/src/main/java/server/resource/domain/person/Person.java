package server.resource.domain.person;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Location;
import server.resource.validators.PersonValidator;

public final class Person implements Serializable {
    private final int id; // Значение поля должно быть больше 0,
    // Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически
    private final Color eyeColor; // Поле не может быть null
    private final Color hairColor; // Поле не может быть null
    private final Location location; // Поле не может быть null
    private final Date birthday; // Поле может быть null
    private final int weight; // Значение поля должно быть больше 0
    private final Country nationality; // Поле может быть null

    private final int userId;

    public Person(Builder builder) throws InvalidFieldFormatException {
        try {
            id = builder.id;
            userId = builder.userId;
            eyeColor = builder.eyeColor;
            hairColor = builder.hairColor;
            location = builder.location;
            birthday = builder.birthday;
            weight = builder.weight;
            nationality = builder.nationality;
        } catch (NullPointerException e) {
            throw new InvalidFieldFormatException(e);
        }

        if (!PersonValidator.isValidInstance(this)) {
            throw new InvalidFieldFormatException();
        }
    }

    public int getId() { return id; }

    public Color getEyeColor() { return eyeColor; }

    public Color getHairColor() { return hairColor; }

    public Location getLocation() { return location; }

    public Date getBirthday() { return birthday; }

    public int getWeight() { return weight; }

    public Country getNationality() { return nationality; }

    public int getUserID() { return userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        if (birthday == null && person.birthday != null) {
            return false;
        }
        if (nationality == null && person.nationality != null) {
            return false;
        }

        return id == person.id
                && userId == person.userId
                && weight == person.weight
                && eyeColor.equals(person.eyeColor)
                && hairColor.equals(person.hairColor)
                && location.equals(person.location)
                && birthday.equals(person.birthday)
                && nationality.equals(person.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, eyeColor, hairColor, location,
                birthday, weight, nationality);
    }

    @Override
    public String toString() {
        String s = "Человек (ID: " + id + ")";
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

    public static final class Builder {
        private Integer id;
        private Color eyeColor;
        private Color hairColor;
        private Location location;
        private Date birthday;
        private Integer weight;
        private Country nationality;
        private Integer userId;

        public Builder setId(int id) {
            this.id = id;

            return this;
        }

        public Builder setEyeColor(Color eyeColor) {
            this.eyeColor = eyeColor;

            return this;
        }

        public Builder setHairColor(Color hairColor) {
            this.hairColor = hairColor;

            return this;
        }

        public Builder setLocation(Location location) {
            this.location = location;

            return this;
        }

        public Builder setBirthday(Date birthday) {
            this.birthday = birthday;

            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;

            return this;
        }

        public Builder setNationality(Country nationality) {
            this.nationality = nationality;

            return this;
        }

        public Builder setUserId(int userId) {
            this.userId = userId;

            return this;
        }

        public Person build() throws InvalidFieldFormatException {
            return new Person(this);
        }
    }
}