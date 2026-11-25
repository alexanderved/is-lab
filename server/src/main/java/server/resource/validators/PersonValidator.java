package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import server.resource.domain.Location;
import server.resource.domain.person.Color;
import server.resource.domain.person.Country;
import server.resource.domain.person.Person;

@ApplicationScoped
public class PersonValidator extends ResourceValidator<Person> {
    @Inject
    private LocationValidator locationValidator;

    public PersonValidator() {

    }

    @Override
    public boolean isValid(Person res) {
        return isValidInstance(res)
                && locationValidator.isValid(res.getLocation());
    }

    public static boolean isValidInstance(Person person) {
        return person != null
                && validateId(person.getId())
                && validateEyeColor(person.getEyeColor())
                && validateHairColor(person.getHairColor())
                && validateLocation(person.getLocation())
                && validateWeight(person.getWeight())
                && validateNationality(person.getNationality());
    }

    public static boolean validateId(int id) {
        return id > 0;
    }

    public static boolean validateEyeColor(Color eyeColor) {
        return eyeColor != null;
    }

    public static boolean validateHairColor(Color hairColor) {
        return hairColor != null;
    }

    public static boolean validateLocation(Location location) {
        return LocationValidator.isValidInstance(location);
    }

    public static boolean validateWeight(int weight) {
        return weight > 0;
    }

    public static boolean validateNationality(Country nationality) {
        return true;
    }
}
