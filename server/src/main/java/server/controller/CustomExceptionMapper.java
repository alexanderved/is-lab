package server.controller;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import server.exceptions.InvalidFieldFormatException;
import server.exceptions.MalformedTokenException;
import server.resource.dto.ErrorInfo;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();

        if (exception instanceof MalformedTokenException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorInfo(exception.getMessage()))
                    .type("application/json")
                    .build();
        }

        if (exception instanceof InvalidFieldFormatException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo(exception.getMessage()))
                    .type("application/json")
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorInfo(exception.getMessage()))
                .type("application/json")
                .build();
    }
}