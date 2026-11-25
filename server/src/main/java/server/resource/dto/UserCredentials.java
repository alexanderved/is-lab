package server.resource.dto;

import java.io.Serializable;

public record UserCredentials(String username, String password) implements Serializable {
}
