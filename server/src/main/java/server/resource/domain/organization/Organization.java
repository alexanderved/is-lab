package server.resource.domain.organization;

import java.io.Serializable;
import java.util.Objects;

import server.exceptions.InvalidFieldFormatException;
import server.resource.domain.Address;
import server.resource.validators.OrganizationValidator;

public final class Organization implements Serializable {
    private final int id;
    private final Address officialAddress; // Поле может быть null
    private final Long annualTurnover; // Поле не может быть null, Значение поля должно быть больше 0
    private final Long employeesCount; // Поле не может быть null, Значение поля должно быть больше 0
    private final String fullName; // Строка не может быть пустой, Поле может быть null
    private final Float rating; // Поле может быть null, Значение поля должно быть больше 0
    private final OrganizationType type; // Поле не может быть null
    private final Address postalAddress; // Поле может быть null

    private final int userId;

    public Organization(Builder builder) throws InvalidFieldFormatException {
        try {
            id = builder.id;
            userId = builder.userId;
            officialAddress = builder.officialAddress;
            annualTurnover = builder.annualTurnover;
            employeesCount = builder.employeesCount;
            fullName = builder.fullName;
            rating = builder.rating;
            type = builder.type;
            postalAddress = builder.postalAddress;
        } catch (NullPointerException e) {
            throw new InvalidFieldFormatException(e);
        }

        if (!OrganizationValidator.isValidInstance(this)) {
            throw new InvalidFieldFormatException();
        }
    }

    public int getId() {
        return id;
    }

    public Address getOfficialAddress() {
        return officialAddress;
    }

    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    public Long getEmployeesCount() {
        return employeesCount;
    }

    public String getFullName() {
        return fullName;
    }

    public Float getRating() {
        return rating;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public int getUserID() {
        return userId;
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

    public static final class Builder {
        private Integer id;
        private Address officialAddress;
        private Long annualTurnover;
        private Long employeesCount;
        private String fullName;
        private Float rating;
        private OrganizationType type;
        private Address postalAddress;
        private Integer userId;

        public Builder setId(int id) {
            this.id = id;

            return this;
        }

        public Builder setOfficialAddress(Address officialAddress) {
            this.officialAddress = officialAddress;

            return this;
        }

        public Builder setAnnualTurnover(Long annualTurnover) {
            this.annualTurnover = annualTurnover;

            return this;
        }

        public Builder setEmployeesCount(Long employeesCount) {
            this.employeesCount = employeesCount;

            return this;
        }

        public Builder setFullName(String fullName) {
            this.fullName = fullName;

            return this;
        }

        public Builder setRating(Float rating) {
            this.rating = rating;

            return this;
        }

        public Builder setType(OrganizationType type) {
            this.type = type;

            return this;
        }

        public Builder setPostalAddress(Address postalAddress) {
            this.postalAddress = postalAddress;

            return this;
        }

        public Builder setUserId(int userId) {
            this.userId = userId;

            return this;
        }

        public Organization build() {
            return new Organization(this);
        }
    }
}