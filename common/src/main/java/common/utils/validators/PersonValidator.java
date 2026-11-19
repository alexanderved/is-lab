package common.utils.validators;

import common.domain.Color;
import common.domain.Country;
import common.domain.Location;
import common.domain.Person;

public final class PersonValidator {
    public static boolean validate(Person person) {
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
        return PersonTemplateValidator.validateEyeColor(eyeColor);
    }

    public static boolean validateHairColor(Color hairColor) {
        return PersonTemplateValidator.validateHairColor(hairColor);
    }

    public static boolean validateLocation(Location location) {
        return PersonTemplateValidator.validateLocation(location);
    }

    public static boolean validateWeight(int weight) {
        return PersonTemplateValidator.validateWeight(weight);
    }

    public static boolean validateNationality(Country nationality) {
        return PersonTemplateValidator.validateNationality(nationality);
    }
}