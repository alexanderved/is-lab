package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.resource.domain.Address;
import server.resource.domain.Location;

@ApplicationScoped
public class AddressValidator extends ResourceValidator<Address> {
    @Inject
    private LocationValidator locationValidator;

    public AddressValidator() {

    }

    @Override
    public boolean isValid(Address res) {
        return isValidInstance(res)
                && (res.getTown() == null || locationValidator.isValid(res.getTown()));
    }

    public static boolean isValidInstance(Address address) {
        return address != null
                && validateStreet(address.getStreet())
                && validateTown(address.getTown());
    }

    public static boolean validateStreet(String street) {
        return street == null || street.length() <= 112;
    }

    public static boolean validateTown(Location town) {
        return town == null || LocationValidator.isValidInstance(town);
    }
}
