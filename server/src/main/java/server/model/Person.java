package server.model;

import common.domain.Color;
import common.domain.Country;
import common.domain.PersonTemplate;
import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private Color hairColor;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x",    column = @Column(name = "loc_x",    nullable = false)),
            @AttributeOverride(name = "y",    column = @Column(name = "loc_y",    nullable = false)),
            @AttributeOverride(name = "z",    column = @Column(name = "loc_z",    nullable = false)),
            @AttributeOverride(name = "name", column = @Column(name = "loc_name", nullable = false))
    })
    private Location location;

    @Column(name = "birthday")
    private OffsetDateTime birthday;

    @Column(
            name = "weight",
            nullable = false,
            check = @CheckConstraint(constraint = "weight > 0")
    )
    private int weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality")
    private Country nationality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Worker> workers = new ArrayList<>();

    public Person() {

    }

    public Person(PersonTemplate template, User user) {
        eyeColor = template.getEyeColor();
        hairColor = template.getHairColor();
        location = new Location(template.getLocation());
        birthday = template.getBirthday() == null
                ? null
                : OffsetDateTime.ofInstant(template.getBirthday().toInstant(), ZoneOffset.UTC);
        weight = template.getWeight();
        nationality = template.getNationality();
        this.user = user;
    }

    public void update(PersonTemplate template) {
        eyeColor = template.getEyeColor();
        hairColor = template.getHairColor();
        location = new Location(template.getLocation());
        birthday = template.getBirthday() == null
                ? null
                : OffsetDateTime.ofInstant(template.getBirthday().toInstant(), ZoneOffset.UTC);
        weight = template.getWeight();
        nationality = template.getNationality();
    }

    public common.domain.Person toDomain() throws InvalidFieldFormatException {
        PersonTemplate template = new PersonTemplate.Builder()
                .setEyeColor(eyeColor)
                .setHairColor(hairColor)
                .setLocation(location.toDomain())
                .setBirthday(birthday == null
                        ? null
                        : Date.from(birthday.toInstant()))
                .setWeight(weight)
                .setNationality(nationality)
                .build();

        return new common.domain.Person(id, user.getId(), template);
    }

    public int getId() {
        return id;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Location getLocation() {
        return location;
    }

    public OffsetDateTime getBirthday() {
        return birthday;
    }

    public int getWeight() {
        return weight;
    }

    public Country getNationality() {
        return nationality;
    }

    public User getUser() {
        return user;
    }
}