package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Location;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.LocationField;
import proglab.utils.validators.LocationValidator;

/**
 * Парсер для класса {@link Location}.
 */
public final class LocationParser {
    /**
     * Возвращает экземпляр класса, получаемый путем парсинга значений полей.
     * 
     * @param data Значения полей класса
     * @return экземпляр класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
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

    /**
     * Возвращает поле 'x' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'x' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Long parseX(String data) throws DataParserException {
        try {
            return Long.parseLong(data);
        } catch (NumberFormatException | NullPointerException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'y' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'y' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
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

    /**
     * Возвращает поле 'z' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'z' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
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
