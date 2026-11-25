package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import server.resource.domain.Location;

@ApplicationScoped
public class LocationValidator extends ResourceValidator<Location> {
    public LocationValidator() {

    }

    @Override
    public boolean isValid(Location res) {
        return isValidInstance(res);
    }

    public static boolean isValidInstance(Location location) {
        return location != null && validateName(location.getName());
    }

    public static boolean validateName(String name) {
        return name != null;
    }
}
