package server.controller;

import java.util.Date;

import server.resource.domain.person.Color;
import server.resource.domain.person.Country;
import server.resource.domain.Location;
import server.exceptions.InvalidFieldFormatException;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.CollectionOperationFailedException;
import server.exceptions.UnauthorizedRequestException;
import server.resource.dto.AddInfo;
import server.resource.dto.PersonDTO;
import server.service.collection.PersonCollectionService;
import server.service.token.Token;
import server.service.token.TokenService;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class PersonController {
    @Inject
    private PersonCollectionService service;

    @Inject
    private TokenService tokenService;

    public PersonController() {

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
            PersonDTO dto
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        int id = service.add(token, dto);
        UpdateWebSocket.notifyUpdate();

        return Response.status(Response.Status.CREATED).entity(new AddInfo(id)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @Context HttpHeaders headers,
            @PathParam("id") int id,
            PersonDTO dto
    ) throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);

        boolean isUpdated = service.update(token, id, dto);
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