package server.controller;

import common.exceptions.InvalidFieldFormatException;
import common.exceptions.UserNameAlreadyTakenException;
import common.exceptions.UserNotFoundException;
import common.network.responses.ErrorResponse;
import io.minio.errors.MinioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import server.exceptions.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({InvalidFieldFormatException.class, UserNameAlreadyTakenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidData(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({UserDBAccessFailedException.class,
            RepositoryAccessFailedException.class,
            CollectionOperationFailedException.class,
            NumberFormatException.class,
            MinioException.class,
            IOException.class,
            NoSuchAlgorithmException.class,
            InvalidKeyException.class})
    public ResponseEntity<ErrorResponse> handleInternalError(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleMissingResource(Exception e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({InvalidRefreshTokenException.class, MalformedTokenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
