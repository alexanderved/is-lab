package server.repository.postgresql.dto;

import common.domain.*;
import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "organizations")
@Check(constraints = "(street IS NULL) = (town IS NULL)")
public class OrganizationDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "name", nullable = false)
    @Check(constraints = "LENGTH(name) > 0")
    private String name;

    @Column(name = "coordinates", nullable = false)
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;

    @Column(name = "annual_turnover")
    @Check(constraints = "annual_turnover > 0")
    private Float annualTurnover;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "employees_count", nullable = false)
    @Check(constraints = "employees_count > 0")
    private int employeesCount;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationType type;

    @Column(name = "street")
    @Check(constraints = "LENGTH(street) > 0")
    private String street;

    @Column(name = "town")
    private Location town;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;

    public OrganizationDTO() {

    }

    public OrganizationDTO(OrganizationTemplate template, UserDTO user) {
        name = template.getName();
        coordinates = template.getCoordinates();
        creationDate = ZonedDateTime.now().toOffsetDateTime();
        annualTurnover = template.getAnnualTurnover();
        fullName = template.getFullName();
        employeesCount = template.getEmployeesCount();
        type = template.getType();
        street = template.getOfficialAddress().getStreet();
        town = template.getOfficialAddress().getTown();
        this.user = user;
    }

    public void update(OrganizationTemplate template) {
        name = template.getName();
        coordinates = template.getCoordinates();
        annualTurnover = template.getAnnualTurnover();
        fullName = template.getFullName();
        employeesCount = template.getEmployeesCount();
        type = template.getType();
        street = template.getOfficialAddress().getStreet();
        town = template.getOfficialAddress().getTown();
    }

    public Organization toDomain() throws InvalidFieldFormatException {
        OrganizationTemplate template = new OrganizationTemplate.Builder()
                .setName(name)
                .setCoordinates(coordinates)
                .setAnnualTurnover(annualTurnover)
                .setFullName(fullName)
                .setEmployeesCount(employeesCount)
                .setType(type)
                .setOfficialAddress(new Address(street, town))
                .build();

        return new Organization(id, creationDate.toZonedDateTime(), user.getId(), template);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public Float getAnnualTurnover() {
        return annualTurnover;
    }

    public String getFullName() {
        return fullName;
    }

    public int getEmployeesCount() {
        return employeesCount;
    }

    public OrganizationType getType() {
        return type;
    }

    public String getStreet() {
        return street;
    }

    public Location getTown() {
        return town;
    }

    public UserDTO getUser() {
        return user;
    }
}
