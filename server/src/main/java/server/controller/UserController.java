package server.controller;

import common.exceptions.UserNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import server.exceptions.RepositoryAccessFailedException;
import server.service.user.UserInfo;
import server.service.user.UserService;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    private UserService userService;

    public UserController() {

    }

    @GET
    public Response list(@QueryParam("filter") @DefaultValue("") String filter)
            throws UserNotFoundException, RepositoryAccessFailedException {
        List<UserInfo> users = userService.load()
                .stream()
                .map(UserInfo::fromUser)
                .filter((u) -> u.username().contains(filter))
                .toList();

        return Response.ok(users).build();
    }

    @GET
    @Path("/{username}")
    public Response findByName(@PathParam("username") String username)
            throws UserNotFoundException, RepositoryAccessFailedException {
        return Response.ok(UserInfo.fromUser(userService.findByName(username))).build();
    }
}