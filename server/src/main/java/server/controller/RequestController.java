package server.controller;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import common.network.responses.SuccessResponse;
import org.springframework.http.HttpHeaders;
import server.service.token.Token;
import server.service.token.TokenService;
import server.service.auth.User;
import common.exceptions.InvalidFieldFormatException;
import common.network.responses.AddResponse;
import common.network.responses.NetworkResponse;
import org.springframework.web.bind.annotation.*;
import server.service.CollectionService;
import server.controller.dto.OrganizationDTO;
import server.exceptions.CollectionOperationFailedException;
import server.service.auth.HashedUser;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final CollectionService collectionService;
    private final TokenService tokenService;

    public RequestController(CollectionService collectionService, TokenService tokenService) {
        this.collectionService = collectionService;
        this.tokenService = tokenService;
    }

    @GetMapping("/organizations")
    public List<Organization> list() {
        return collectionService.getSortedCollection();
    }

    @PostMapping("/organizations")
    public SuccessResponse add(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                               @RequestBody OrganizationDTO org)
            throws InvalidFieldFormatException, CollectionOperationFailedException {
        User user = org.getUser();
        OrganizationTemplate template = org.toDomain();

        String tokenString = authHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        return new AddResponse(collectionService.add(token, template));
    }
}
