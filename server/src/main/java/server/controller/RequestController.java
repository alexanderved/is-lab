package server.controller;

import common.domain.Organization;
import common.domain.OrganizationTemplate;
import common.network.responses.SuccessResponse;
import org.springframework.http.HttpHeaders;
import server.service.token.Token;
import server.service.token.TokenService;
import common.exceptions.InvalidFieldFormatException;
import common.network.responses.AddResponse;
import org.springframework.web.bind.annotation.*;
import server.service.CollectionService;
import server.exceptions.CollectionOperationFailedException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final CollectionService collectionService;
    private final TokenService tokenService;

    public RequestController(CollectionService collectionService,
                             TokenService tokenService) {
        this.collectionService = collectionService;
        this.tokenService = tokenService;
    }

    @GetMapping("/organizations")
    public List<Organization> list(@RequestParam(defaultValue = "ASC") String order,
                                   @RequestParam(defaultValue = "id") String column,
                                   @RequestParam(name = "per-page-number", defaultValue = "1")
                                       String perPageNumberStr,
                                   @RequestParam(name = "page-count", defaultValue = "1") String pageCountStr,
                                   @RequestParam(defaultValue = "") String filter) {
        int perPageNumber = Integer.parseInt(perPageNumberStr);
        int pageCount = Integer.parseInt(pageCountStr);

        return collectionService.getCollection(order, column, perPageNumber, pageCount, filter);
    }

    @PostMapping("/organizations")
    public SuccessResponse add(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                               @RequestBody OrganizationTemplate template)
            throws InvalidFieldFormatException, CollectionOperationFailedException {
        String tokenString = authHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        return new AddResponse(collectionService.add(token, template));
    }

    @DeleteMapping("/organizations/{id}")
    public SuccessResponse remove(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                  @PathVariable int id)
            throws CollectionOperationFailedException {
        String tokenString = authHeader.split(" ")[1].trim();
        Token token = tokenService.parseTokenString(tokenString);

        collectionService.removeById(token, id);

        return new SuccessResponse();
    }
}
