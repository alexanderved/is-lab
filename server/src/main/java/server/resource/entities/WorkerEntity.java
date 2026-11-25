package server.resource.entities;

import jakarta.persistence.*;
import server.resource.domain.worker.Position;
import server.resource.domain.worker.Status;

import java.util.Date;

@Entity
@Table(name = "workers")
public class WorkerEntity implements OwnedByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(
            name = "name",
            nullable = false,
            check = @CheckConstraint(constraint = "LENGTH(name) > 0")
    )
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "coord_x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "coord_y", nullable = false))
    })
    private CoordinatesEntity coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @Column(
            name = "salary",
            nullable = false,
            check = @CheckConstraint(constraint = "salary > 0")
    )
    private int salary;

    @Column(
            name = "rating",
            check = @CheckConstraint(constraint = "rating IS NULL OR rating > 0")
    )
    private Long rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public WorkerEntity() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CoordinatesEntity getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public int getSalary() {
        return salary;
    }

    public Long getRating() {
        return rating;
    }

    public Position getPosition() {
        return position;
    }

    public Status getStatus() {
        return status;
    }

    public PersonEntity getPerson() {
        return person;
    }

    @Override
    public UserEntity getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(CoordinatesEntity coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void hire(OrganizationEntity organization) {
        setOrganization(organization);
    }

    public void indexSalary(double coefficient) {
        long newSalary = Math.round(getSalary() * coefficient);
        setSalary((int) Math.max(1L, newSalary));
    }
}