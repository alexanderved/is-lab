package common.utils.fields;

import java.util.List;
import java.util.stream.Stream;

import common.domain.OrganizationType;

/**
 * Перечисление полей класса {@link Organization}.
 */
public enum OrganizationField implements Field {
    ID("id", "ID"),
    NAME("name", "название"),
    COORDINATES("coordinates", "координаты"),
    CREATION_DATE("creationDate", "время создания"),
    ANNUAL_TURNOVER("annualTurnover", "годовой оборот"),
    FULL_NAME("fullName", "полное название"),
    EMPLOYEES_COUNT("employeesCount", "число сотрудников"),
    TYPE("type", "тип", getTypeExtraInfo()),
    OFFICIAL_ADDRESS("officialAddress", "официальный адрес");

    private final String name;
    private final String displayName;
    private final String extra;

    private OrganizationField(String name, String displayName) {
        this(name, displayName, null);
    }

    private OrganizationField(String name, String displayName, String extra) {
        this.name = name;
        this.displayName = displayName;
        this.extra = extra;
    }

    /**
     * Возвращает поле класса, соответствующее данному имени.
     * 
     * @param name Имя поля
     * @return поле класса, соответствующее данному имени.
     * @throws IllegalArgumentException если отсутствует поле с данным именем
     */
    public static OrganizationField fromName(String name) {
        for (OrganizationField orgFields : OrganizationField.values()) {
            if (orgFields.getName().equals(name)) {
                return orgFields;
            }
        }

        throw new IllegalArgumentException("Отсутствует константа `OrganizationField` с названием " + name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getExtra() {
        return extra;
    }

    private static String getTypeExtraInfo() {
        List<String> typeStrings = Stream.of(OrganizationType.values())
                .map(OrganizationType::toString)
                .toList();

        return String.join("/", typeStrings);
    }
}
