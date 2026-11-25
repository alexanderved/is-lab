package server.resource.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import server.resource.domain.Coordinates;

@Embeddable
public class CoordinatesEntity {
    @Column(name = "coord_x", nullable = false)
    private int x;

    @Column(name = "coord_y", nullable = false)
    private float y;

    public CoordinatesEntity() {
    }

    public int getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}