package proglab.utils.parsers;

import java.util.Map;

import proglab.domain.Coordinates;
import proglab.exceptions.DataParserException;
import proglab.exceptions.InvalidFieldFormatException;
import proglab.utils.fields.CoordinatesField;
import proglab.utils.validators.CoordinatesValidator;

/**
 * Парсер для класса {@link Coordinates}.
 */
public final class CoordinatesParser {
    /**
     * Возвращает экземпляр класса, получаемый путем парсинга значений полей.
     * 
     * @param data Значения полей класса
     * @return экземпляр класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Coordinates parse(Map<CoordinatesField, Object> data)
            throws DataParserException {
        try {
            return new Coordinates((Float) data.get(CoordinatesField.X), (Integer) data.get(CoordinatesField.Y));
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
    public static Float parseX(String data) throws DataParserException {
        try {
            return Float.parseFloat(data);
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
