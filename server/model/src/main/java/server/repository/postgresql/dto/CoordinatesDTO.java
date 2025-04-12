package server.repository.postgresql.dto;

import common.domain.Coordinates;
import common.exceptions.InvalidFieldFormatException;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Locale;

public class CoordinatesDTO {
    private final Float x;
    private final Integer y;

    private CoordinatesDTO(Coordinates c) {
        x = c.getX();
        y = c.getY();
    }

    private CoordinatesDTO(Float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public static CoordinatesDTO fromCoordinates(Coordinates c) {
        return new CoordinatesDTO(c);
    }

    public Coordinates toCoordinates() throws InvalidFieldFormatException {
        try {
            return new Coordinates(x, y);
        } catch (NullPointerException e) {
            throw new InvalidFieldFormatException("Поле `x` не может быть null");
        }
    }

    public static CoordinatesDTO fromPGobject(PGobject pgobj) {
        String[] values = pgobj.getValue()
                .substring(1, pgobj.getValue().length() - 1)
                .split(",");

        float x = Float.parseFloat(values[0]);
        Integer y = Integer.parseInt(values[1]);

        return new CoordinatesDTO(x, y);
    }

    public PGobject toPGobject() throws SQLException {
        PGobject pgobj = new PGobject();
        pgobj.setType("coordinates");
        pgobj.setValue(String.format(Locale.US, "(%f,%d)", x, y));

        return pgobj;
    }
}
