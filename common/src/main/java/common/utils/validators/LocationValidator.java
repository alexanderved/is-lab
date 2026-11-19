package common.utils.validators;

import common.domain.Location;

public final class LocationValidator {
    public static boolean validate(Location location) {
        return location != null
            && validateName(location.getName());
    }

    public static boolean validateName(String name) {
        return name != null;
    }
}
