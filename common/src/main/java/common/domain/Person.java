package common.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import common.utils.Validatable;
import common.utils.validators.PersonValidator;

/**
 * Класс человека.
 */
public final class Person implements Validatable, Comparable<Person>, Serializable {
    private final int id; // Значение поля должно быть больше 0,
    // Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически
    private Color eyeColor; // Поле не может быть null
    private Color hairColor; // Поле не может быть null
    private Location location; // Поле не может быть null
    private Date birthday; // Поле может быть null
    private int weight; // Значение поля должно быть больше 0
    private Country nationality; // Поле может быть null

    private final int userId;

    /**
     * @param id ID человека
     * @param userId ID пользователя, добавившего человека
     * @param template Шаблон человека
     */
    public Person(int id, int userId, PersonTemplate template) {
        if (id <= 0) {
            throw new IllegalArgumentException("Поле `id` класса `Person`"
                    + " должно быть больше 0");
        }
        if (template == null) {
            throw new IllegalArgumentException("Шаблон `PersonTemplate` не может быть null");
        }

        this.id = id;
        this.userId = userId;
        eyeColor = template.getEyeColor();
        hairColor = template.getHairColor();
        location = template.getLocation();
        birthday = template.getBirthday();
        weight = template.getWeight();
        nationality = template.getNationality();
    }

    /**
     * Обновляет данные человека.
     * @param template Шаблон новых данных
     */
    public void update(PersonTemplate template) {
        eyeColor = template.getEyeColor();
        hairColor = template.getHairColor();
        location = template.getLocation();
        birthday = template.getBirthday();
        weight = template.getWeight();
        nationality = template.getNationality();
    }

    /** @return ID человека */
    public int getId() { return id; }

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

    /** @return ID пользователя, добавившего человека */
    public int getUserID() { return userId; }

    @Override
    public boolean isValid() {
        return PersonValidator.validate(this);
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
}