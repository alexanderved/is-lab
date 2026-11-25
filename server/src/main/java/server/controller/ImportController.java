package server.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.persistence.internal.oxm.schema.model.Import;
import server.exceptions.ImportException;
import server.exceptions.UnauthorizedRequestException;
import server.resource.domain.import_operation.ImportOperation;
import server.resource.dto.ErrorInfo;
import server.service.import_operation.ImportHistoryService;
import server.service.import_operation.ImportService;
import server.service.token.Token;
import server.service.token.TokenService;

import java.io.InputStream;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin", "superadmin"})
public class ImportController {
    @Inject
    private TokenService tokenService;

    @Inject
    private ImportService importService;

    @Inject
    private ImportHistoryService importHistoryService;

    public ImportController() {
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importFile(@FormParam("file") EntityPart filePart,
                               @Context HttpHeaders headers) {
        Token token;
        try {
            token = getToken(headers);
        } catch (UnauthorizedRequestException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try (InputStream stream = filePart.getContent()) {
            ImportOperation result = importService.importFile(token, stream);

            return Response.ok(result).build();
        } catch (ImportException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorInfo(e.getMessage()))
                    .build();
        } finally {
            UpdateWebSocket.notifyUpdate();
        }
    }

    @GET
    @Path("/history")
    public Response history(@Context HttpHeaders headers) throws UnauthorizedRequestException {
        Token token = getToken(headers);

        return Response.ok(importHistoryService.history(token)).build();
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
