package server.resource.domain.import_operation;

import java.io.Serializable;

public class ImportOperation implements Serializable {
    private final int id;
    private final ImportOperationStatus status;
    private final Integer objectsCount;

    private final int userId;

    public ImportOperation(int id, ImportOperationStatus status, Integer objectsCount, int userId) {
        this.id = id;
        this.status = status;
        this.objectsCount = objectsCount;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public ImportOperationStatus getStatus() {
        return status;
    }

    public Integer getObjectsCount() {
        return objectsCount;
    }

    public int getUserId() {
        return userId;
    }
}
