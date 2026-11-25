package server.resource.dto;

import java.io.Serializable;

public record LocationDTO(int x, int y, double z, String name) implements Serializable {
}
