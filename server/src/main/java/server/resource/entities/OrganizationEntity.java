package server.resource.entities;

import server.exceptions.InvalidFieldFormatException;
import jakarta.persistence.*;
import server.resource.domain.organization.Organization;
import server.resource.domain.organization.OrganizationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "organizations")
public class OrganizationEntity implements OwnedByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",    column = @Column(name = "official_street", length = 112)),
            @AttributeOverride(name = "town.x",    column = @Column(name = "official_town_x")),
            @AttributeOverride(name = "town.y",    column = @Column(name = "official_town_y")),
            @AttributeOverride(name = "town.z",    column = @Column(name = "official_town_z")),
            @AttributeOverride(name = "town.name", column = @Column(name = "official_town_name"))
    })
    private AddressEntity officialAddress;

    @Column(
            name = "annual_turnover",
            nullable = false,
            check = @CheckConstraint(constraint = "annual_turnover > 0")
    )
    private Long annualTurnover;

    @Column(
            name = "employees_count",
            nullable = false,
            check = @CheckConstraint(constraint = "employees_count > 0")
    )
    private Long employeesCount;

    @Column(
            name = "full_name",
            check = @CheckConstraint(constraint = "full_name IS NULL OR LENGTH(full_name) > 0")
    )
    private String fullName;

    @Column(
            name = "rating",
            check = @CheckConstraint(constraint = "rating IS NULL OR rating > 0")
    )
    private Float rating;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",    column = @Column(name = "postal_street", length = 112)),
            @AttributeOverride(name = "town.x",    column = @Column(name = "postal_town_x")),
            @AttributeOverride(name = "town.y",    column = @Column(name = "postal_town_y")),
            @AttributeOverride(name = "town.z",    column = @Column(name = "postal_town_z")),
            @AttributeOverride(name = "town.name", column = @Column(name = "postal_town_name"))
    })
    private AddressEntity postalAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<WorkerEntity> workers = new ArrayList<>();

    public OrganizationEntity() {
    }

    public int getId() {
        return id;
    }

    public AddressEntity getOfficialAddress() {
        return officialAddress;
    }

    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    public Long getEmployeesCount() {
        return employeesCount;
    }

    public String getFullName() {
        return fullName;
    }

    public Float getRating() {
        return rating;
    }

    public OrganizationType getType() {
        return type;
    }

    public AddressEntity getPostalAddress() {
        return postalAddress;
    }

    @Override
    public UserEntity getUser() {
        return user;
    }

    public List<WorkerEntity> getWorkers() {
        return Collections.unmodifiableList(workers);
    }

    public void setOfficialAddress(AddressEntity officialAddress) {
        this.officialAddress = officialAddress;
    }

    public void setAnnualTurnover(Long annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public void setEmployeesCount(Long employeesCount) {
        this.employeesCount = employeesCount;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public void setPostalAddress(AddressEntity postalAddress) {
        this.postalAddress = postalAddress;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}