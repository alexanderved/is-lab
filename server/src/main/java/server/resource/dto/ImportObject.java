package server.resource.dto;

import jakarta.json.JsonValue;

import java.io.Serializable;

public class ImportObject implements Serializable {
    public final String resType;
    public final JsonValue body;

    public ImportObject(String resType, JsonValue body) {
        this.resType = resType;
        this.body = body;
    }
}
