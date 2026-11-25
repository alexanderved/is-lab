package server.resource.dto;

import java.io.Serializable;

public record ErrorInfo(String message) implements Serializable {
}
