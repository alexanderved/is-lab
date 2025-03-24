package common.utils.parsers;

import java.util.Map;

import common.domain.Address;
import common.domain.Coordinates;
import common.domain.OrganizationTemplate;
import common.domain.OrganizationType;
import common.exceptions.DataParserException;
import common.exceptions.InvalidFieldFormatException;
import common.utils.fields.AddressField;
import common.utils.fields.CoordinatesField;
import common.utils.fields.OrganizationField;
import common.utils.validators.OrganizationValidator;

/**
 * Парсер для класса {@link OrganizationTemplate}.
 */
public final class OrganizationTemplateParser {
    /**
     * Возвращает экземпляр класса, получаемый путем парсинга значений полей.
     * 
     * @param data Значения полей класса
     * @return экземпляр класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static OrganizationTemplate parse(Map<OrganizationField, Object> data)
            throws DataParserException {
        OrganizationTemplate.Builder builder = new OrganizationTemplate.Builder();

        try {
            builder.setName((String) data.get(OrganizationField.NAME));
            builder.setCoordinates((Coordinates) data.get(OrganizationField.COORDINATES));
            builder.setAnnualTurnover((Float) data.get(OrganizationField.ANNUAL_TURNOVER));
            builder.setFullName((String) data.get(OrganizationField.FULL_NAME));
            builder.setEmployeesCount((Integer) data.get(OrganizationField.EMPLOYEES_COUNT));
            builder.setType((OrganizationType) data.get(OrganizationField.TYPE));
            builder.setOfficialAddress((Address) data.get(OrganizationField.OFFICIAL_ADDRESS));
        } catch (InvalidFieldFormatException | ClassCastException | NullPointerException e) {
            throw new DataParserException(e);
        }

        try {
            return builder.build();
        } catch (IllegalStateException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'name' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'name' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static String parseName(String data) throws DataParserException {
        if (!OrganizationValidator.validateName(data)) {
            throw new DataParserException(
                    new InvalidFieldFormatException(OrganizationField.NAME.getName()));
        }

        return data;
    }

    /**
     * Возвращает поле 'coordinates' класса, получаемое путем парсинга полей класса {@link Coordinates}.
     * 
     * @param data поля класса {@code Coordinates}
     * @return поле 'coordinates' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Coordinates parseCoordinates(Map<CoordinatesField, Object> data)
            throws DataParserException {
        return CoordinatesParser.parse(data);
    }

    /**
     * Возвращает поле 'annualTurnover' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'annualTurnover' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Float parseAnnualTurnover(String data) throws DataParserException {
        try {
            Float annualTurnover = Float.parseFloat(data);
            if (!OrganizationValidator.validateAnnualTurnover(annualTurnover)) {
                throw new InvalidFieldFormatException(OrganizationField.ANNUAL_TURNOVER.getName());
            }

            return annualTurnover;
        } catch (NumberFormatException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'fullName' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'fullName' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static String parseFullName(String data) throws DataParserException {
        return data;
    }

    /**
     * Возвращает поле 'employeesCount' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'employeesCount' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Integer parseEmployeesCount(String data) throws DataParserException {
        try {
            int employeesCount = Integer.parseInt(data);
            if (!OrganizationValidator.validateEmployeesCount(employeesCount)) {
                throw new InvalidFieldFormatException(OrganizationField.EMPLOYEES_COUNT.getName());
            }

            return employeesCount;
        } catch (NumberFormatException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'type' класса, получаемое путем парсинга строки.
     * 
     * @param data строка
     * @return поле 'type' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static OrganizationType parseType(String data) throws DataParserException {
        try {
            OrganizationType type = OrganizationType.valueOf(data);
            if (!OrganizationValidator.validateType(type)) {
                throw new InvalidFieldFormatException(OrganizationField.TYPE.getName());
            }

            return type;
        } catch (IllegalArgumentException | NullPointerException
                | InvalidFieldFormatException e) {
            throw new DataParserException(e);
        }
    }

    /**
     * Возвращает поле 'officialAddress' класса, получаемое путем парсинга полей класса {@link Address}.
     * 
     * @param data поля класса {@code Address}
     * @return поле 'officialAddress' класса
     * @throws DataParserException если в процессе парсинга было выброшено
     *                             исключение
     */
    public static Address parseOfficialAddress(Map<AddressField, Object> data)
            throws DataParserException {
        return AddressParser.parse(data);
    }
}
