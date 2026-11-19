package server.controller;

import common.domain.OrganizationTemplate;
import common.utils.validators.OrganizationTemplateValidator;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnauthorizedRequestException;
import server.service.collection.OrganizationCollectionService;
import server.service.token.Token;
import server.service.token.TokenService;

@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class OrganizationController {
    @Inject
    private OrganizationCollectionService service;

    @Inject
    private TokenService tokenService;

    public OrganizationController() {

    }

    @GET
    public Response list(
            @QueryParam("order") @DefaultValue("ASC") String order,
            @QueryParam("column") @DefaultValue("id") String column,
            @QueryParam("pageNumber") @DefaultValue("1") int pageNumber,
            @QueryParam("perPageNumber") @DefaultValue("20") int perPageNumber,
            @QueryParam("pageCount") @DefaultValue("1") int pageCount,
            @QueryParam("filter") @DefaultValue("") String filter) {
        return Response.ok(service.getCollection(
                order, column, pageNumber, perPageNumber, pageCount, filter)).build();
    }

    @GET
    @Path("/count")
    public Response count(
            @QueryParam("column") @DefaultValue("id") String column,
            @QueryParam("filter") @DefaultValue("") String filter) {
        return Response.ok(service.count(column, filter)).build();
    }

    @POST
    public Response add(
            @Context HttpHeaders headers,
            OrganizationTemplate template
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        if (!OrganizationTemplateValidator.validate(template)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int id = service.add(token, template);
        UpdateWebSocket.notifyUpdate();

        return Response.status(Response.Status.CREATED).entity(id).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @Context HttpHeaders headers,
            @PathParam("id") int id,
            @RequestBody OrganizationTemplate template
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        if (!OrganizationTemplateValidator.validate(template)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean isUpdated = service.update(token, id, template);
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

        boolean isRemoved = service.removeById(token, id);
        if (isRemoved) {
            UpdateWebSocket.notifyUpdate();
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
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