package common.utils.validators;

import common.domain.Color;
import common.domain.Country;
import common.domain.Location;
import common.domain.PersonTemplate;

public final class PersonTemplateValidator {
    public static boolean validate(PersonTemplate template) {
        return template != null
                && validateEyeColor(template.getEyeColor())
                && validateHairColor(template.getHairColor())
                && validateLocation(template.getLocation())
                && validateWeight(template.getWeight())
                && validateNationality(template.getNationality());
    }

    public static boolean validateEyeColor(Color eyeColor) {
        return eyeColor != null;
    }

    public static boolean validateHairColor(Color hairColor) {
        return hairColor != null;
    }

    public static boolean validateLocation(Location location) {
        return LocationValidator.validate(location);
    }

    public static boolean validateWeight(int weight) {
        return weight > 0;
    }

    public static boolean validateNationality(Country nationality) {
        return true; // nullable
    }
}