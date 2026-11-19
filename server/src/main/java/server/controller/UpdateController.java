package server.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import server.service.update.UpdateService;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class UpdateController {

    @Inject
    private UpdateService updateService;

    public UpdateController() {
    }

    @GET
    @Path("/wait")
    public void waitUpdate(@Context SecurityContext securityContext,
                           @Suspended AsyncResponse res) {
        String username = securityContext.getUserPrincipal().getName();
        updateService.waitUpdate(username, res);
    }

    @POST
    @Path("/notify")
    public Response notifyUpdate(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        updateService.notifyUpdate(username);
        return Response.ok().build();
    }
}