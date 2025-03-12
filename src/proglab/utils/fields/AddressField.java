package proglab.utils.fields;

/**
 * Перечисление полей класса {@link Address}.
 */
public enum AddressField implements Field {
    STREET("street", "улица"),
    TOWN("town", "координаты города");

    private final String name;
    private final String displayName;
    private final String extra;

    private AddressField(String name, String displayName) {
        this(name, displayName, null);
    }

    private AddressField(String name, String displayName, String extra) {
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
    public static AddressField fromName(String name) {
        for (AddressField addrFields : AddressField.values()) {
            if (addrFields.getName().equals(name)) {
                return addrFields;
            }
        }

        throw new IllegalArgumentException("Отсутствует константа `AddressField` с названием " + name);
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
