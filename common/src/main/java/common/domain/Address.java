package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.AddressValidator;

public final class Address implements Validatable, Serializable {
    private final String street; // Длина строки не должна быть больше 112, Поле может быть null
    private final Location town; // Поле может быть null

    private Address() {
        street = null;
        town = null;
    }

    public Address(String street, Location town) throws InvalidFieldFormatException {
        if (!AddressValidator.validateStreet(street)) {
            throw new InvalidFieldFormatException("street",
                    "Длина строки в поле `street` не должна быть больше 112");
        }
        this.street = street;

        if (!AddressValidator.validateTown(town)) {
            throw new InvalidFieldFormatException("town",
                    "Поле `town` не валидно");
        }
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public Location getTown() {
        return town;
    }

    @Override
    public boolean isValid() {
        return AddressValidator.validate(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address a = (Address)o;

        return street.equals(a.street) && town.equals(a.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, town);
    }

    @Override
    public String toString() {
        if (street == null && town == null) {
            return "";
        } else if (street != null && town == null) {
            return street;
        } else if (street == null) {
            return town.toString();
        } else {
            return street + ", " + town.toString();
        }
    }
}