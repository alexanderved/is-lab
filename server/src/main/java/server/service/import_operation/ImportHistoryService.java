package server.service.import_operation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.exceptions.RepositoryAccessFailedException;
import server.exceptions.UserNotFoundException;
import server.repository.ImportOperationRepository;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.domain.import_operation.ImportOperationStatus;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;
import server.resource.dto.ImportOperationDTO;
import server.resource.dto.OrganizationDTO;
import server.resource.dto.PersonDTO;
import server.resource.dto.WorkerDTO;
import server.service.UserService;
import server.service.token.Token;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportHistoryService {
    @Inject
    private ImportOperationRepository importOperationRepository;

    @Inject
    private UserService userService;

    public ImportHistoryService() {
    }

    @Transactional
    public ImportOperation recordSuccess(Token token, List<OrganizationDTO> organizationDTOs,
                              List<PersonDTO> personDTOs, List<WorkerDTO> workerDTOs) {
        return importOperationRepository.add(new ImportOperationDTO(ImportOperationStatus.SUCCESS,
                organizationDTOs.size() + personDTOs.size() + countObjectsInWorkers(workerDTOs)), token);
    }

    @Transactional
    public ImportOperation recordFailure(Token token) {
        return importOperationRepository.add(new ImportOperationDTO(ImportOperationStatus.FAILED, null), token);
    }

    @Transactional
    public List<ImportOperation> history(Token token) {
        User user = null;
        try {
            user = userService.findByName(token.getUsername());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (user.getRole() == Role.ROLE_USER) {
            return importOperationRepository.loadByUsername(token.getUsername());
        } else if (user.getRole() == Role.ROLE_BLOCKED) {
            return new ArrayList<>();
        } else {
            return importOperationRepository.load();
        }
    }

    private static int countObjectsInWorkers(List<WorkerDTO> workerDTOs) {
        int innerCount = 0;
        for (var worker : workerDTOs) {
            if (worker.organizationId() == null && worker.organization() != null) {
                ++innerCount;
            }

            if (worker.personId() == null && worker.person() != null) {
                ++innerCount;
            }
        }

        return workerDTOs.size() + innerCount;
    }
}
