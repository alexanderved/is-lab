package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Address;
import proglab.domain.Location;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.AddressField;
import proglab.utils.fields.LocationField;
import proglab.utils.validators.AddressValidator;

public final class AddressParser {
    public static Address parse(Map<AddressField, Object> data) throws DataParserException {
        try {
            return new Address((String)data.get(AddressField.STREET),
                (Location)data.get(AddressField.TOWN));
        } catch (InvalidFieldFormatException | ClassCastException
                | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    public static String parseStreet(String data) throws DataParserException {
        if (!AddressValidator.validateStreet(data)) {
            throw new DataParserException(
                new InvalidFieldFormatException(AddressField.STREET.getName()));
        }

        return data;
    }

    public static Location parseTown(Map<LocationField, Object> data)
        throws DataParserException
    {
        return LocationParser.parse(data);
    }
}
