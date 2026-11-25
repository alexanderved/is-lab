package server.resource.entities;

import server.exceptions.InvalidFieldFormatException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import server.resource.domain.Location;

@Embeddable
public class LocationEntity {
    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Column(nullable = false)
    private double z;

    @Column(nullable = false)
    private String name;

    public LocationEntity() {
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