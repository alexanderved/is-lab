package server.repository.postgresql.dto;

import common.domain.Location;
import common.exceptions.InvalidFieldFormatException;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class LocationDTO {
    private final Long x;
    private final Long y;
    private final Integer z;

    private LocationDTO(Location l) {
        x = l.getX();
        y = l.getY();
        z = l.getZ();
    }

    private LocationDTO(Long x, Long y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public static LocationDTO fromPGobject(PGobject pgobj) {
        String[] values = pgobj.getValue()
                .substring(1, pgobj.getValue().length() - 1)
                .split(",");

        long x = Long.parseLong(values[0]);
        Long y = Long.parseLong(values[1]);
        Integer z = Integer.parseInt(values[2]);

        return new LocationDTO(x, y, z);
    }

    public PGobject toPGobject() throws SQLException {
        PGobject pgobj = new PGobject();
        pgobj.setType("location");
        pgobj.setValue(String.format("(%d,%d,%d)", x, y, z));

        return pgobj;
    }
}
