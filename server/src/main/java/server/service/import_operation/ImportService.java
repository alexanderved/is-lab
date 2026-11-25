package server.service.import_operation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import server.exceptions.ImportException;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.dto.ImportObject;
import server.service.token.Token;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportService {
    @Inject
    private ImportExecutor importExecutor;

    @Inject
    private ImportHistoryService importHistoryService;

    public ImportOperation importFile(Token token, InputStream stream) {
        List<ImportObject> entries;
        try {
            entries = parse(stream);
        } catch (ImportException e) {
            importHistoryService.recordFailure(token);
            throw e;
        } catch (Exception e) {
            importHistoryService.recordFailure(token);
            throw new ImportException("Ошибка парсинга файла: " + e.getMessage());
        }

        try {
            return importExecutor.execute(token, entries);
        } catch (Exception e) {
            importHistoryService.recordFailure(token);
            throw new ImportException("Неверный формат файла");
        }
    }

    private List<ImportObject> parse(InputStream stream) {
        JsonArray root;
        try {
            root = Json.createReader(stream).readArray();
        } catch (Exception e) {
            throw new ImportException("Корень файла должен быть массивом [...]");
        }

        if (root.isEmpty()) {
            throw new ImportException("Файл пуст");
        }

        List<ImportObject> entries = new ArrayList<>(root.size());

        for (int i = 0; i < root.size(); i++) {
            String position = "Элемент " + (i + 1);

            JsonValue raw = root.get(i);
            if (raw.getValueType() != JsonValue.ValueType.OBJECT) {
                throw new ImportException(position + ": ожидался объект {\"type\":..., \"body\":...}");
            }
            JsonObject obj = (JsonObject) raw;

            String type = obj.getString("type", null);
            if (type == null || type.isBlank()) {
                throw new ImportException(position + ": отсутствует поле 'type'");
            }

            JsonValue bodyVal = obj.get("body");
            if (bodyVal == null) {
                throw new ImportException(position + ": отсутствует поле 'body'");
            }

            if (bodyVal.getValueType() != JsonValue.ValueType.OBJECT) {
                throw new ImportException(position + ": поле 'body' должно быть объектом");
            }

            entries.add(new ImportObject(type, bodyVal));
        }

        return entries;
    }
}
