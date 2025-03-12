package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Location;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.LocationField;
import proglab.utils.validators.LocationValidator;

public final class LocationParser {
    public static Location parse(Map<LocationField, Object> data)
            throws DataParserException {
        try {
            return new Location((Long) data.get(LocationField.X),
                    (Long) data.get(LocationField.Y), (Integer) data.get(LocationField.Z));
        } catch (InvalidFieldFormatException | ClassCastException
                | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    public static Long parseX(String data) throws DataParserException {
        try {
            return Long.parseLong(data);
        } catch (NumberFormatException | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    public static Long parseY(String data) throws DataParserException {
        try {
            Long y = Long.parseLong(data);
            if (!LocationValidator.validateY(y)) {
                throw new InvalidFieldFormatException(LocationField.Y.getName());
            }

            return y;
        } catch (NumberFormatException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }

    public static Integer parseZ(String data) throws DataParserException {
        try {
            Integer z = Integer.parseInt(data);
            if (!LocationValidator.validateZ(z)) {
                throw new InvalidFieldFormatException(LocationField.Z.getName());
            }

            return z;
        } catch (NumberFormatException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }
}
