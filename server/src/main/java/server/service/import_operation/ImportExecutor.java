package server.service.import_operation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import server.exceptions.ImportException;
import server.repository.OrganizationRepository;
import server.repository.PersonRepository;
import server.repository.WorkerRepository;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.dto.*;
import server.service.token.Token;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportExecutor {
    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private WorkerRepository workerRepository;

    @Inject
    private ImportHistoryService importHistoryService;

    public ImportExecutor() {
    }

    @Transactional
    public ImportOperation execute(Token token, List<ImportObject> importObjects) {
        List<OrganizationDTO> organizationDTOs = new ArrayList<>();
        List<PersonDTO> personDTOs = new ArrayList<>();
        List<WorkerDTO> workerDTOs = new ArrayList<>();

        filter(importObjects, organizationDTOs, personDTOs, workerDTOs);

        organizationRepository.bulkAdd(organizationDTOs, token);
        personRepository.bulkAdd(personDTOs, token);
        workerRepository.bulkAdd(workerDTOs, token);

        return importHistoryService.recordSuccess(token, organizationDTOs, personDTOs, workerDTOs);
    }

    private static void filter(List<ImportObject> importObjects, List<OrganizationDTO> organizationDTOs,
                        List<PersonDTO> personDTOs, List<WorkerDTO> workerDTOs)
    {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            for (var importObj : importObjects) {
                switch (importObj.resType) {
                    case "organization" -> deserialize(importObj.body, organizationDTOs, OrganizationDTO.class, jsonb);
                    case "person" -> deserialize(importObj.body, personDTOs, PersonDTO.class, jsonb);
                    case "worker" -> deserialize(importObj.body, workerDTOs, WorkerDTO.class, jsonb);
                    default -> throw new ImportException("Неизвестный тип: " + importObj.resType);
                }
            }
        } catch (Exception e) {
            throw new ImportException(e);
        }
    }

    private static <T> void deserialize(JsonValue value, List<T> dtoList, Class<T> dtoClass, Jsonb jsonb) {
        T dto = jsonb.fromJson(value.toString(), dtoClass);
        dtoList.add(dto);
    }
}
