package server.controller;

import server.exceptions.UserNotFoundException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.RepositoryAccessFailedException;
import server.service.AdminService;
import server.resource.domain.user.Role;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "superadmin"})
public class AdminController {
    @Inject
    private AdminService adminService;

    public AdminController() {

    }

    @POST
    @Path("/appoint/{id}")
    public Response appoint(@PathParam("id") int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        adminService.changeUserRole(id, (role) ->
                switch (role) {
                    case ROLE_USER -> Role.ROLE_ADMIN;
                    case ROLE_ADMIN -> Role.ROLE_USER;
                    default -> role;
                });

        return Response.ok().build();
    }

    @POST
    @Path("/block/{id}")
    public Response block(@PathParam("id") int id)
            throws UserNotFoundException, RepositoryAccessFailedException {
        adminService.changeUserRole(id, (role) ->
                switch (role) {
                    case ROLE_BLOCKED -> Role.ROLE_USER;
                    case ROLE_SUPERADMIN -> Role.ROLE_SUPERADMIN;
                    default -> Role.ROLE_BLOCKED;
                });

        return Response.ok().build();
    }
}