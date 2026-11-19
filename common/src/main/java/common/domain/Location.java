package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.exceptions.InvalidFieldFormatException;
import common.utils.Validatable;
import common.utils.validators.LocationValidator;

/**
 * Класс координат локации в трехмерном пространстве.
 */
public final class Location implements Validatable, Serializable {
    private final int x;
    private final int y;
    private final double z;
    private final String name; // Поле не может быть null

    private Location() {
        x = 0;
        y = 0;
        z = 0.0;
        name = "";
    }

    public Location(int x, int y, double z, String name)
            throws InvalidFieldFormatException {
        this.x = x;
        this.y = y;
        this.z = z;

        if (!LocationValidator.validateName(name)) {
            throw new InvalidFieldFormatException("name", "Поле `name` не может null");
        }
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isValid() {
        return LocationValidator.validate(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location l = (Location)o;

        return x == l.x && y == l.y && z == l.z && name.equals(l.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, name);
    }

    @Override
    public String toString() {
        return name + ", " + "(" + x + ", " + y + ", " + z + ")";
    }
}