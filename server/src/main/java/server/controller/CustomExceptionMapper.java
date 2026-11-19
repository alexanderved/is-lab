package server.controller;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import server.exceptions.MalformedTokenException;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();

        if (exception instanceof MalformedTokenException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(exception.getMessage())
                    .type("text/plain")
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .type("text/plain")
                .build();
    }
}