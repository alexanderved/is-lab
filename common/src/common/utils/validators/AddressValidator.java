package common.utils.validators;

import common.domain.Address;
import common.domain.Location;

public final class AddressValidator {
    public static boolean validate(Address address) {
        return address != null
            && validateStreet(address.getStreet())
            && address.getTown() != null;
    }

    public static boolean validateStreet(String street) {
        return street == null || !street.isEmpty();
    }

    public static boolean validateTown(Location town) {
        return LocationValidator.validate(town);
    }
}