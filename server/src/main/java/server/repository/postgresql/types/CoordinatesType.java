package server.repository.postgresql.types;

import common.domain.Coordinates;
import common.exceptions.InvalidFieldFormatException;
import org.hibernate.annotations.TypeRegistration;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@TypeRegistration(basicClass = Coordinates.class, userType = CoordinatesType.class)
public class CoordinatesType implements UserType<Coordinates> {
    @Override
    public int getSqlType() {
        return SqlTypes.VARCHAR;
    }

    @Override
    public Class<Coordinates> returnedClass() {
        return Coordinates.class;
    }

    @Override
    public boolean equals(Coordinates x, Coordinates y) {
        return x.equals(y);
    }

    @Override
    public int hashCode(Coordinates coordinates) {
        return coordinates.hashCode();
    }

    @Override
    public Coordinates nullSafeGet(ResultSet resultSet, int i,
                                   SharedSessionContractImplementor session,
                                   Object o)
            throws SQLException {
        String s = resultSet.getString(i);
        if (resultSet.wasNull()) {
            return null;
        }

        s = s.substring(1, s.length() - 1);
        String[] values = s.split(",");

        float x = Float.parseFloat(values[0]);
        Integer y = Integer.parseInt(values[1]);

        try {
            return new Coordinates(x, y);
        } catch (InvalidFieldFormatException e) {
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Coordinates c, int i,
                            SharedSessionContractImplementor session)
            throws SQLException {
        if (c == null) {
            preparedStatement.setNull(i, SqlTypes.VARCHAR);
        } else {
            preparedStatement.setString(i, "(" + c.getX() + "," + c.getY() + ")");
        }
    }

    @Override
    public Coordinates deepCopy(Coordinates coordinates) {
        return coordinates;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Coordinates coordinates) {
        return coordinates;
    }

    @Override
    public Coordinates assemble(Serializable serializable, Object o) {
        return (Coordinates) serializable;
    }
}
