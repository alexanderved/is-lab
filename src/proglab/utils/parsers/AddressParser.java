package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Address;
import proglab.domain.Location;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.AddressField;
import proglab.utils.fields.LocationField;
import proglab.utils.validators.AddressValidator;

/**
 * Парсер для класса {@link Address}.
 */
public final class AddressParser {
    /**
     * Возвращает экземпляр класса, получаемый путем парсинга значений полей.
     * 
     * @param data Значения полей класса
     * @return экземпляр класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Address parse(Map<AddressField, Object> data) throws DataParserException {
        try {
            return new Address((String) data.get(AddressField.STREET),
                    (Location) data.get(AddressField.TOWN));
        } catch (InvalidFieldFormatException | ClassCastException
                | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'street' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'street' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static String parseStreet(String data) throws DataParserException {
        if (!AddressValidator.validateStreet(data)) {
            throw new DataParserException(
                    new InvalidFieldFormatException(AddressField.STREET.getName()));
        }

        return data;
    }

    /**
     * Возвращает поле 'town' класса, получаемое путем парсинга полей класса {@link Location}.
     * 
     * @param data поля класса {@code Location}
     * @return поле 'town' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Location parseTown(Map<LocationField, Object> data)
            throws DataParserException {
        return LocationParser.parse(data);
    }
}
