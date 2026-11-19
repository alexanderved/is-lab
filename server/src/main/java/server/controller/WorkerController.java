package server.controller;

import common.domain.Coordinates;
import common.domain.Person;
import common.domain.Position;
import common.domain.Status;
import common.domain.WorkerTemplate;
import common.exceptions.InvalidFieldFormatException;

import common.network.responses.ErrorResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnauthorizedRequestException;
import server.service.collection.OrganizationCollectionService;
import server.service.collection.PersonCollectionService;
import server.service.collection.WorkerCollectionService;
import server.service.token.Token;
import server.service.token.TokenService;

@Path("/workers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class WorkerController {
    @Inject
    private WorkerCollectionService workerService;

    @Inject
    private OrganizationCollectionService organizationService;

    @Inject
    private PersonCollectionService personService;

    @Inject
    private TokenService tokenService;

    public WorkerController() {

    }

    @GET
    public Response list(
            @QueryParam("order") @DefaultValue("ASC") String order,
            @QueryParam("column") @DefaultValue("id") String column,
            @QueryParam("pageNumber") @DefaultValue("1") int pageNumber,
            @QueryParam("perPageNumber") @DefaultValue("20") int perPageNumber,
            @QueryParam("pageCount") @DefaultValue("1") int pageCount,
            @QueryParam("filter") @DefaultValue("") String filter) {
        return Response.ok(workerService.getCollection(
                order, column, pageNumber, perPageNumber, pageCount, filter)).build();
    }

    @GET
    @Path("/count")
    public Response count(
            @QueryParam("column") @DefaultValue("id") String column,
            @QueryParam("filter") @DefaultValue("") String filter) {
        return Response.ok(workerService.count(column, filter)).build();
    }

    @POST
    public Response add(
            @Context HttpHeaders headers,
            WorkerTemplateDTO body
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        WorkerTemplate template;
        try {
            template = body.toTemplate(organizationService, personService);
        } catch (InvalidFieldFormatException | IllegalArgumentException e) {
            System.out.println(e.toString());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage() == null
                            ? "Invalid worker data"
                            : e.getMessage()))
                    .build();
        }

        int id = workerService.add(token, template);
        UpdateWebSocket.notifyUpdate();

        return Response.status(Response.Status.CREATED).entity(id).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @Context HttpHeaders headers,
            @PathParam("id") int id,
            WorkerTemplateDTO body
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        WorkerTemplate template;
        try {
            template = body.toTemplate(organizationService, personService);
        } catch (InvalidFieldFormatException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage() == null
                            ? "Invalid worker data"
                            : e.getMessage()))
                    .build();
        }

        boolean isUpdated = workerService.update(token, id, template);
        if (isUpdated) {
            UpdateWebSocket.notifyUpdate();
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remove(@Context HttpHeaders headers, @PathParam("id") int id)
            throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        boolean isRemoved = workerService.removeById(token, id);
        if (isRemoved) {
            UpdateWebSocket.notifyUpdate();
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static final class WorkerTemplateDTO {
        public String name;
        public CoordinatesDTO coordinates;
        public Integer organizationId;
        public Integer salary;
        public Long rating;
        public Position position;
        public Status status;
        public Integer personId;

        public WorkerTemplate toTemplate(OrganizationCollectionService organizationService,
                                         PersonCollectionService personService)
                throws InvalidFieldFormatException {
            common.domain.Organization organization = organizationId == null
                    ? null
                    : organizationService.getCollection("ASC", "id", 1, Integer.MAX_VALUE, 1, "")
                    .stream()
                    .filter((o) -> o.getId() == organizationId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Организация с id " + organizationId + " не найдена"));

            Person person = personService.getCollection("ASC", "id", 1, Integer.MAX_VALUE, 1, "")
                    .stream()
                    .filter((p) -> p.getId() == personId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Человек с id " + personId + " не найден"));

            return new WorkerTemplate.Builder()
                    .setName(name)
                    .setCoordinates(coordinates.toDomain())
                    .setOrganization(organization)
                    .setSalary(salary)
                    .setRating(rating)
                    .setPosition(position)
                    .setStatus(status)
                    .setPerson(person)
                    .build();
        }
    }

    public static final class CoordinatesDTO {
        public int x;
        public float y;

        public Coordinates toDomain() {
            return new Coordinates(x, y);
        }
    }

    private Token getToken(HttpHeaders headers) throws UnauthorizedRequestException {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedRequestException();
        }

        String tokenString = authorizationHeader.split(" ")[1].trim();
        return tokenService.parseTokenString(tokenString);
    }
}