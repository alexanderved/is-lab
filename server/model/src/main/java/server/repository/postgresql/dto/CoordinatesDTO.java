package server.repository.postgresql.dto;

import common.domain.Coordinates;
import common.exceptions.InvalidFieldFormatException;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

public class CoordinatesDTO implements Struct {
    private final Float x;
    private final Integer y;

    private CoordinatesDTO(Coordinates c) {
        x = c.getX();
        y = c.getY();
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

    @Override
    public String getSQLTypeName() throws SQLException {
        return "coordinates";
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        return new Object[]{ x, y };
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return getAttributes();
    }
}
