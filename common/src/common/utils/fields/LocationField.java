package common.utils.fields;

/**
 * Перечисление полей класса {@link Location}.
 */
public enum LocationField implements Field {
    X("x", "X"),
    Y("y", "Y"),
    Z("z", "Z");

    private final String name;
    private final String displayName;
    private final String extra;

    private LocationField(String name, String displayName) {
        this(name, displayName, null);
    }

    private LocationField(String name, String displayName, String extra) {
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
    public static LocationField fromName(String name) {
        for (LocationField locFields : LocationField.values()) {
            if (locFields.getName().equals(name)) {
                return locFields;
            }
        }

        throw new IllegalArgumentException("Отсутствует константа `LocationField` с названием " + name);
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
}
