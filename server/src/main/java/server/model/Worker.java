package server.model;

import common.domain.Position;
import common.domain.Status;
import common.domain.WorkerTemplate;
import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.*;
import org.eclipse.persistence.annotations.CascadeOnDelete;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "workers")
public class Worker {
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
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private OffsetDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Worker() {

    }

    public Worker(
            WorkerTemplate template,
            Organization organization,
            Person person,
            User user
    ) {
        name = template.getName();
        coordinates = new Coordinates(template.getCoordinates());
        creationDate = OffsetDateTime.now();
        this.organization = organization;
        salary = template.getSalary();
        rating = template.getRating();
        position = template.getPosition();
        status = template.getStatus();
        this.person = person;
        this.user = user;
    }

    public void update(WorkerTemplate template, Organization organization, Person person) {
        name = template.getName();
        coordinates = new Coordinates(template.getCoordinates());
        this.organization = organization;
        salary = template.getSalary();
        rating = template.getRating();
        position = template.getPosition();
        status = template.getStatus();
        this.person = person;
    }

    public common.domain.Worker toDomain() throws InvalidFieldFormatException {
        WorkerTemplate template = new WorkerTemplate.Builder()
                .setName(name)
                .setCoordinates(coordinates.toDomain())
                .setOrganization(organization == null ? null : organization.toDomain())
                .setSalary(salary)
                .setRating(rating)
                .setPosition(position)
                .setStatus(status)
                .setPerson(person.toDomain())
                .build();

        return new common.domain.Worker(
                id,
                Date.from(creationDate.toInstant()),
                user.getId(),
                template
        );
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

    public Organization getOrganization() {
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

    public Person getPerson() {
        return person;
    }

    public User getUser() {
        return user;
    }

    public void hire(Organization organization) {
        this.organization = organization;
    }

    public void indexSalary(double coefficient) {
        long newSalary = Math.round(this.salary * coefficient);
        this.salary = (int) Math.max(1L, newSalary);
    }
}