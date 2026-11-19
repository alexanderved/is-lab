package server.model;

import common.exceptions.InvalidFieldFormatException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class Address {
    @Column(length = 112)
    private String street;

    @Embedded
    private Location town;

    public Address() {
    }

    public Address(common.domain.Address source) {
        if (source == null) {
            return;
        }
        this.street = source.getStreet();
        this.town = source.getTown() == null ? null : new Location(source.getTown());
    }

    public common.domain.Address toDomain() throws InvalidFieldFormatException {
        return new common.domain.Address(
                street,
                town == null ? null : town.toDomain()
        );
    }

    public String getStreet() {
        return street;
    }

    public Location getTown() {
        return town;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setTown(Location town) {
        this.town = town;
    }
}