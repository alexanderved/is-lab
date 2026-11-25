package server.resource.entities;

import server.exceptions.InvalidFieldFormatException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import server.resource.domain.Address;

@Embeddable
public class AddressEntity {
    @Column(length = 112)
    private String street;

    @Embedded
    private LocationEntity town;

    public AddressEntity() {
    }

    public String getStreet() {
        return street;
    }

    public LocationEntity getTown() {
        return town;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setTown(LocationEntity town) {
        this.town = town;
    }
}