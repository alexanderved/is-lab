package common.utils.validators;

import java.util.Collection;
import java.util.HashSet;

import common.domain.Person;

public final class PersonCollectionValidator {
    public static boolean validate(Collection<Person> collection) {
        HashSet<Integer> ids = new HashSet<>();

        for (Person org : collection) {
            if (!PersonValidator.validate(org)) {
                return false;
            }

            ids.add(org.getId());
        }

        return collection.size() == ids.size();
    }
}
