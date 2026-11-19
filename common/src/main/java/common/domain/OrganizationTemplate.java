package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.OrganizationTemplateValidator;

/**
 * Шаблон организации, представляющий собой набор данных этой организации.
 */
public final class OrganizationTemplate implements Validatable, Comparable<Organization>, Serializable {
    private Address officialAddress;
    private Long annualTurnover;
    private Long employeesCount;
    private String fullName;
    private Float rating;
    private OrganizationType type;
    private Address postalAddress;

    private OrganizationTemplate() {

    }

    private OrganizationTemplate(Builder builder) {
        officialAddress = builder.officialAddress;
        annualTurnover = builder.annualTurnover;
        employeesCount = builder.employeesCount;
        fullName = builder.fullName;
        rating = builder.rating;
        type = builder.type;
        postalAddress = builder.postalAddress;
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

    @Override
    public boolean isValid() {
        return OrganizationTemplateValidator.validate(this);
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

        OrganizationTemplate org = (OrganizationTemplate) o;

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

        return annualTurnover.equals(org.annualTurnover)
                && employeesCount.equals(org.employeesCount)
                && fullName.equals(org.fullName)
                && rating.equals(org.rating)
                && type.equals(org.type)
                && officialAddress.equals(org.officialAddress)
                && postalAddress.equals(org.postalAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annualTurnover, employeesCount, fullName,
                rating, type, officialAddress, postalAddress);
    }

    @Override
    public String toString() {
        String s = "Шаблон Организации";
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

    /**
     * Строитель экземпляров класса {@code OrganizationTemplate}.
     */
    public static final class Builder {
        private Address officialAddress;
        private Long annualTurnover;
        private Long employeesCount;
        private String fullName;
        private Float rating;
        private OrganizationType type;
        private Address postalAddress;

        /**
         * Устанавливает официальный адрес организации.
         *
         * @param officialAddress Официальный адрес организации
         * @return Строитель экземпляров класса
         */
        public Builder setOfficialAddress(Address officialAddress) {
            this.officialAddress = officialAddress;

            return this;
        }

        /**
         * Устанавливает годовой оборот организации.
         *
         * @param annualTurnover Годовой оборот организации
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setAnnualTurnover(Long annualTurnover)
                throws InvalidFieldFormatException {
            if (!OrganizationTemplateValidator.validateAnnualTurnover(annualTurnover)) {
                throw new InvalidFieldFormatException("annualTurnover");
            }
            this.annualTurnover = annualTurnover;

            return this;
        }

        /**
         * Устанавливает число сотрудников организации.
         *
         * @param employeesCount Число сотрудников организации
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setEmployeesCount(Long employeesCount)
                throws InvalidFieldFormatException {
            if (!OrganizationTemplateValidator.validateEmployeesCount(employeesCount)) {
                throw new InvalidFieldFormatException("employeesCount");
            }
            this.employeesCount = employeesCount;

            return this;
        }

        /**
         * Устанавливает полное название организации.
         *
         * @param fullName Полное название организации
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setFullName(String fullName) throws InvalidFieldFormatException {
            if (!OrganizationTemplateValidator.validateFullName(fullName)) {
                throw new InvalidFieldFormatException("fullName");
            }
            this.fullName = fullName;

            return this;
        }

        /**
         * Устанавливает рейтинг организации.
         *
         * @param rating Рейтинг организации
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setRating(Float rating) throws InvalidFieldFormatException {
            if (!OrganizationTemplateValidator.validateRating(rating)) {
                throw new InvalidFieldFormatException("rating");
            }
            this.rating = rating;

            return this;
        }

        /**
         * Устанавливает тип организации.
         *
         * @param type Тип организации
         * @return Строитель экземпляров класса
         * @throws InvalidFieldFormatException если поле не удовлетворяет формату
         */
        public Builder setType(OrganizationType type) throws InvalidFieldFormatException {
            if (!OrganizationTemplateValidator.validateType(type)) {
                throw new InvalidFieldFormatException("type");
            }
            this.type = type;

            return this;
        }

        /**
         * Устанавливает почтовый адрес организации.
         *
         * @param postalAddress Почтовый адрес организации
         * @return Строитель экземпляров класса
         */
        public Builder setPostalAddress(Address postalAddress) {
            this.postalAddress = postalAddress;

            return this;
        }

        /**
         * Проверяет, установлены ли все обязательные поля класса.
         *
         * @return {@code true}, если все обязательные поля класс установлены
         */
        public boolean isComplete() {
            return annualTurnover != null && employeesCount != null && type != null;
        }

        /**
         * Создает экземпляр класса {@code OrganizationTemplate}.
         *
         * @return экземпляр класса {@code OrganizationTemplate}
         * @throws IllegalStateException если не все обязательные поля класса были установлены
         */
        public OrganizationTemplate build() {
            if (!isComplete()) {
                throw new IllegalStateException(
                        "Метод `build` класса `OrganizationTemplate.Builder` не может быть"
                                + " вызван пока не будут установлены все обязательные поля");
            }

            return new OrganizationTemplate(this);
        }
    }
}