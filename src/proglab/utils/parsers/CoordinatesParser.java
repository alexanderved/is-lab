package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Coordinates;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.CoordinatesField;
import proglab.utils.validators.CoordinatesValidator;

public final class CoordinatesParser {
    public static Coordinates parse(Map<CoordinatesField, Object> data)
            throws DataParserException {
        try {
            return new Coordinates((Float) data.get(CoordinatesField.X), (Integer) data.get(CoordinatesField.Y));
        } catch (InvalidFieldFormatException | ClassCastException
                | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    public static Float parseX(String data) throws DataParserException {
        try {
            return Float.parseFloat(data);
        } catch (NumberFormatException | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    public static Integer parseY(String data) throws DataParserException {
        try {
            Integer y = Integer.parseInt(data);
            if (!CoordinatesValidator.validateY(y)) {
                throw new InvalidFieldFormatException(CoordinatesField.Y.getName());
            }

            return y;
        } catch (NumberFormatException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }
}
