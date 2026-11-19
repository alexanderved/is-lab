package common.domain;

import java.io.Serializable;
import java.util.Objects;

import common.utils.Validatable;

public final class Coordinates implements Validatable, Serializable {
    private final int x;
    private final float y;

    private Coordinates() {
        x = 0;
        y = 0;
    }

    public Coordinates(int x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coordinates c = (Coordinates)o;

        return x == c.x && y == c.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}