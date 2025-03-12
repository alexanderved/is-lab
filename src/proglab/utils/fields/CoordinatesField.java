package proglab.utils.fields;

public enum CoordinatesField implements Field {
    X("x", "X"),
    Y("y", "Y");

    private final String name;
    private final String displayName;
    private final String extra;

    private CoordinatesField(String name, String displayName) {
        this(name, displayName, null);
    }

    private CoordinatesField(String name, String displayName, String extra) {
        this.name = name;
        this.displayName = displayName;
        this.extra = extra;
    }

    public static CoordinatesField fromName(String name) {
        for (CoordinatesField coordsFields : CoordinatesField.values()) {
            if (coordsFields.getName().equals(name)) {
                return coordsFields;
            }
        }

        throw new IllegalArgumentException("Отсутствует константа `CoordinatesField` с названием " + name);
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
