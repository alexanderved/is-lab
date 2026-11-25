package server.resource.entities;

import jakarta.persistence.*;
import server.resource.domain.import_operation.ImportOperationStatus;

@Entity
@Table(name = "import_operations")
public class ImportOperationEntity implements OwnedByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImportOperationStatus status;

    @Column(name = "objects_count")
    private Integer objectsCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public ImportOperationEntity() {
    }

    public int getId() {
        return id;
    }

    public ImportOperationStatus getStatus() {
        return status;
    }

    public void setStatus(ImportOperationStatus status) {
        this.status = status;
    }

    public Integer getObjectsCount() {
        return objectsCount;
    }

    public void setObjectsCount(Integer objectsCount) {
        this.objectsCount = objectsCount;
    }

    @Override
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
