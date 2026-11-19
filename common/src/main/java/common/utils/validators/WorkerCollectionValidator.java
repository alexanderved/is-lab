package common.utils.validators;

import java.util.Collection;
import java.util.HashSet;

import common.domain.Worker;

public final class WorkerCollectionValidator {
    public static boolean validate(Collection<Worker> collection) {
        HashSet<Integer> ids = new HashSet<>();

        for (Worker org : collection) {
            if (!WorkerValidator.validate(org)) {
                return false;
            }

            ids.add(org.getId());
        }

        return collection.size() == ids.size();
    }
}
