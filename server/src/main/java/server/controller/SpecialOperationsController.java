package server.controller;

import common.domain.Person;
import common.domain.Worker;
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
import server.service.operations.SpecialOperationsService;
import server.service.token.Token;
import server.service.token.TokenService;

import java.util.List;
import java.util.Map;

@Path("/operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class SpecialOperationsController {

    @Inject
    private SpecialOperationsService service;

    @Inject
    private TokenService tokenService;

    public SpecialOperationsController() {
    }

    @GET
    @Path("/rating/sum")
    public Response ratingSum() {
        return Response.ok(Map.of("value", service.calculateRatingSum())).build();
    }

    @GET
    @Path("/rating/average")
    public Response ratingAverage() {
        return Response.ok(Map.of("value", service.calculateRatingAverage())).build();
    }

    @GET
    @Path("/unique-persons")
    public Response uniquePersons() {
        List<Person> persons = service.getUniquePersons();
        return Response.ok(persons).build();
    }

    @POST
    @Path("/hire/{workerId}/{organizationId}")
    public Response hire(@Context HttpHeaders headers,
                         @PathParam("workerId") int workerId,
                         @PathParam("organizationId") int organizationId)
            throws CollectionOperationFailedException, UnauthorizedRequestException {
        Token token = getToken(headers);
        Worker updated = service.hire(token, workerId, organizationId);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Worker or organization not found, "
                            + "or you don't have permission to modify this worker"))
                    .build();
        }
        return Response.ok(updated).build();
    }

    @POST
    @Path("/index-salary/{workerId}")
    public Response indexSalary(@Context HttpHeaders headers,
                                @PathParam("workerId") int workerId,
                                @QueryParam("coefficient") double coefficient)
            throws CollectionOperationFailedException, UnauthorizedRequestException {
        if (coefficient <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Coefficient must be positive"))
                    .build();
        }
        Token token = getToken(headers);
        Worker updated = service.indexSalary(token, workerId, coefficient);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Worker not found, "
                            + "or you don't have permission to modify this worker"))
                    .build();
        }
        return Response.ok(updated).build();
    }

    private Token getToken(HttpHeaders headers) throws UnauthorizedRequestException {
        String auth = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (auth == null || auth.isBlank()) {
            throw new UnauthorizedRequestException();
        }
        return tokenService.parseTokenString(auth.split(" ")[1].trim());
    }
}