package server.model;

import common.domain.*;
import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization {
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
    private Address officialAddress;

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
    private Address postalAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Worker> workers = new ArrayList<>();

    public Organization() {

    }

    public Organization(OrganizationTemplate template, User user) {
        officialAddress = template.getOfficialAddress() == null
                ? null : new Address(template.getOfficialAddress());
        annualTurnover = template.getAnnualTurnover();
        employeesCount = template.getEmployeesCount();
        fullName = template.getFullName();
        rating = template.getRating();
        type = template.getType();
        postalAddress = template.getPostalAddress() == null
                ? null : new Address(template.getPostalAddress());
        this.user = user;
    }

    public Organization(common.domain.Organization org, User user) {
        id = org.getId();
        officialAddress = org.getOfficialAddress() == null
                ? null : new Address(org.getOfficialAddress());
        annualTurnover = org.getAnnualTurnover();
        employeesCount = org.getEmployeesCount();
        fullName = org.getFullName();
        rating = org.getRating();
        type = org.getType();
        postalAddress = org.getPostalAddress() == null
                ? null : new Address(org.getPostalAddress());
        this.user = user;
    }

    public void update(OrganizationTemplate template) {
        officialAddress = template.getOfficialAddress() == null
                ? null : new Address(template.getOfficialAddress());
        annualTurnover = template.getAnnualTurnover();
        employeesCount = template.getEmployeesCount();
        fullName = template.getFullName();
        rating = template.getRating();
        type = template.getType();
        postalAddress = template.getPostalAddress() == null
                ? null : new Address(template.getPostalAddress());
    }

    public common.domain.Organization toDomain() throws InvalidFieldFormatException {
        OrganizationTemplate template = new OrganizationTemplate.Builder()
                .setOfficialAddress(officialAddress == null ? null : officialAddress.toDomain())
                .setAnnualTurnover(annualTurnover)
                .setEmployeesCount(employeesCount)
                .setFullName(fullName)
                .setRating(rating)
                .setType(type)
                .setPostalAddress(postalAddress == null ? null : postalAddress.toDomain())
                .build();

        return new common.domain.Organization(id, user.getId(), template);
    }

    public int getId() {
        return id;
    }

    public Address getOfficialAddress() {
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

    public Address getPostalAddress() {
        return postalAddress;
    }

    public User getUser() {
        return user;
    }

    public List<Worker> getWorkers() {
        return workers;
    }
}