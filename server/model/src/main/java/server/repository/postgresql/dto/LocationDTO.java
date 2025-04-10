package server.repository.postgresql.dto;

import common.domain.Location;
import common.exceptions.InvalidFieldFormatException;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

public class LocationDTO implements Struct {
    private final Long x;
    private final Long y;
    private final Integer z;

    private LocationDTO(Location l) {
        x = l.getX();
        y = l.getY();
        z = l.getZ();
    }

    public static LocationDTO fromLocation(Location l) {
        return new LocationDTO(l);
    }

    public Location toLocation() throws InvalidFieldFormatException {
        try {
            return new Location(x, y, z);
        } catch (NullPointerException e) {
            throw new InvalidFieldFormatException("Поле `x` не может быть null");
        }
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return "location";
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        return new Object[]{ x, y, z };
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return getAttributes();
    }
}
