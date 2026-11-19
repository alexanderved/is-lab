package common.utils.validators;

import common.domain.Address;
import common.domain.Location;

public final class AddressValidator {
    public static boolean validate(Address address) {
        return address != null
            && validateStreet(address.getStreet())
            && validateTown(address.getTown());
    }

    public static boolean validateStreet(String street) {
        return street == null || street.length() <= 112;
    }

    public static boolean validateTown(Location town) {
        return town == null || town.isValid();
    }
}