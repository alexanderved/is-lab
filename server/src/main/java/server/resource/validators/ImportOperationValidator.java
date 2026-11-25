package server.resource.validators;

import jakarta.enterprise.context.ApplicationScoped;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.domain.import_operation.ImportOperationStatus;

@ApplicationScoped
public class ImportOperationValidator extends ResourceValidator<ImportOperation> {
    @Override
    public boolean isValid(ImportOperation res) {
        return isValidInstance(res);
    }

    public static boolean isValidInstance(ImportOperation res) {
        return res != null
                && res.getId() > 0
                && validateObjectsCountForStatus(res.getObjectsCount(), res.getStatus());
    }

    public static boolean validateObjectsCountForStatus(Integer objectsCount, ImportOperationStatus status) {
        return (status == ImportOperationStatus.SUCCESS && objectsCount != null && objectsCount > 0)
                || (status == ImportOperationStatus.FAILED && objectsCount == null);
    }
}
