package server.resource.entities;

import server.resource.domain.person.Color;
import server.resource.domain.person.Country;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "persons")
public class PersonEntity implements OwnedByUser {
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
    private LocationEntity location;

    @Column(name = "birthday")
    private Date birthday;

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
    private UserEntity user;

    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<WorkerEntity> workers = new ArrayList<>();

    public PersonEntity() {

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

    public LocationEntity getLocation() {
        return location;
    }

    public Date getBirthday() {
        return birthday;
    }

    public int getWeight() {
        return weight;
    }

    public Country getNationality() {
        return nationality;
    }

    @Override
    public UserEntity getUser() {
        return user;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<WorkerEntity> getWorkers() {
        return Collections.unmodifiableList(workers);
    }
}