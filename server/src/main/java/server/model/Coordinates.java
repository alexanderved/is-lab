package server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Coordinates {
    @Column(name = "coord_x", nullable = false)
    private int x;

    @Column(name = "coord_y", nullable = false)
    private float y;

    public Coordinates() {
    }

    public Coordinates(common.domain.Coordinates source) {
        this.x = source.getX();
        this.y = source.getY();
    }

    public common.domain.Coordinates toDomain() {
        return new common.domain.Coordinates(x, y);
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