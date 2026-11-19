package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.utils.Validatable;
import common.utils.validators.OrganizationValidator;

/**
 * Класс организации
 */
public final class Organization implements Validatable, Comparable<Organization>, Serializable {
    private final int id;
    private Address officialAddress; // Поле может быть null
    private Long annualTurnover; // Поле не может быть null, Значение поля должно быть больше 0
    private Long employeesCount; // Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; // Строка не может быть пустой, Поле может быть null
    private Float rating; // Поле может быть null, Значение поля должно быть больше 0
    private OrganizationType type; // Поле не может быть null
    private Address postalAddress; // Поле может быть null

    private final int userId;

    /**
     * @param id ID организации
     * @param userId ID пользователя, добавившего организацию
     * @param template Шаблон организации
     */
    public Organization(int id, int userId, OrganizationTemplate template) {
        if (id <= 0) {
            throw new IllegalArgumentException("Поле `id` класса `Organization`"
                    + " должно быть больше 0");
        }

        this.id = id;
        this.userId = userId;
        officialAddress = template.getOfficialAddress();
        annualTurnover = template.getAnnualTurnover();
        employeesCount = template.getEmployeesCount();
        fullName = template.getFullName();
        rating = template.getRating();
        type = template.getType();
        postalAddress = template.getPostalAddress();
    }

    /**
     * Обновляет данные организации.
     * @param template Шаблон новых данных организации.
     */
    public void update(OrganizationTemplate template) {
        officialAddress = template.getOfficialAddress();
        annualTurnover = template.getAnnualTurnover();
        employeesCount = template.getEmployeesCount();
        fullName = template.getFullName();
        rating = template.getRating();
        type = template.getType();
        postalAddress = template.getPostalAddress();
    }

    /**
     * @return ID организации
     */
    public int getId() {
        return id;
    }

    /**
     * @return официальный адрес организации или null, если данная информация отсутствует
     */
    public Address getOfficialAddress() {
        return officialAddress;
    }

    /**
     * @return годовой оборот организации
     */
    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    /**
     * @return число работников организации
     */
    public Long getEmployeesCount() {
        return employeesCount;
    }

    /**
     * @return полное название организации или null, если данная информация отсутствует
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return рейтинг организации или null, если данная информация отсутствует
     */
    public Float getRating() {
        return rating;
    }

    /**
     * @return тип организации
     */
    public OrganizationType getType() {
        return type;
    }

    /**
     * @return почтовый адрес организации или null, если данная информация отсутствует
     */
    public Address getPostalAddress() {
        return postalAddress;
    }

    /**
     * @return ID пользователя, добавившего организацию
     */
    public int getUserID() {
        return userId;
    }

    @Override
    public boolean isValid() {
        return OrganizationValidator.validate(this);
    }

    @Override
    public int compareTo(Organization org) {
        return Long.compare(getEmployeesCount(), org.getEmployeesCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Organization org = (Organization)o;

        if (annualTurnover == null && org.annualTurnover != null) {
            return false;
        }
        if (fullName == null && org.fullName != null) {
            return false;
        }
        if (rating == null && org.rating != null) {
            return false;
        }
        if (officialAddress == null && org.officialAddress != null) {
            return false;
        }
        if (postalAddress == null && org.postalAddress != null) {
            return false;
        }

        return id == org.id
                && userId == org.userId
                && annualTurnover.equals(org.annualTurnover)
                && employeesCount.equals(org.employeesCount)
                && fullName.equals(org.fullName)
                && rating.equals(org.rating)
                && type.equals(org.type)
                && officialAddress.equals(org.officialAddress)
                && postalAddress.equals(org.postalAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, annualTurnover, employeesCount,
                fullName, rating, type, officialAddress, postalAddress);
    }

    @Override
    public String toString() {
        String s = "Организация (ID: " + id + ")";
        s += "\n\tГодовой оборот: " + annualTurnover;
        s += "\n\tЧисло сотрудников: " + employeesCount;
        if (fullName != null) {
            s += "\n\tПолное название: " + fullName;
        }
        if (rating != null) {
            s += "\n\tРейтинг: " + rating;
        }
        s += "\n\tТип: " + type;
        if (officialAddress != null) {
            s += "\n\tОфициальный адрес: " + officialAddress;
        }
        if (postalAddress != null) {
            s += "\n\tПочтовый адрес: " + postalAddress;
        }

        return s;
    }
}