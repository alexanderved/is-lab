package server.repository.postgresql.types;

import common.domain.Location;
import common.exceptions.InvalidFieldFormatException;
import org.hibernate.annotations.TypeRegistration;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@TypeRegistration(basicClass = Location.class, userType = LocationType.class)
public class LocationType implements UserType<Location> {
    @Override
    public int getSqlType() {
        return SqlTypes.VARCHAR;
    }

    @Override
    public Class<Location> returnedClass() {
        return Location.class;
    }

    @Override
    public boolean equals(Location x, Location y) {
        return x.equals(y);
    }

    @Override
    public int hashCode(Location location) {
        return location.hashCode();
    }

    @Override
    public Location nullSafeGet(ResultSet resultSet, int i,
                                SharedSessionContractImplementor session,
                                Object o)
            throws SQLException {
        String s = resultSet.getString(i);
        if (resultSet.wasNull()) {
            return null;
        }

        s = s.substring(1, s.length() - 1);
        String[] values = s.split(",");

        long x = Long.parseLong(values[0]);
        Long y = Long.parseLong(values[1]);
        Integer z = Integer.parseInt(values[2]);

        try {
            return new Location(x, y, z);
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Location l, int i,
                            SharedSessionContractImplementor session)
            throws SQLException {
        if (l == null) {
            preparedStatement.setNull(i, SqlTypes.VARCHAR);
        } else {
            preparedStatement.setString(i, "(" + l.getX() + "," + l.getY() + "," + l.getZ() + ")");
        }
    }

    @Override
    public Location deepCopy(Location location) {
        return location;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Location location) {
        return location;
    }

    @Override
    public Location assemble(Serializable serializable, Object o) {
        return (Location) serializable;
    }
}
