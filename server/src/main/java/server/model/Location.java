package server.model;

import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Column(nullable = false)
    private double z;

    @Column(nullable = false)
    private String name;

    public Location() {
    }

    public Location(common.domain.Location source) {
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
        this.name = source.getName();
    }

    public common.domain.Location toDomain() throws InvalidFieldFormatException {
        return new common.domain.Location(x, y, z, name);
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setName(String name) {
        this.name = name;
    }
}