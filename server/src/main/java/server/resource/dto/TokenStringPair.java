package server.resource.dto;

import java.io.Serializable;

public record TokenStringPair(String access, String refresh) implements Serializable {
}
