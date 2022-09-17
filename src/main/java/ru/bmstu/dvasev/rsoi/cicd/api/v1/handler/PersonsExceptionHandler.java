package ru.bmstu.dvasev.rsoi.cicd.api.v1.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.bmstu.dvasev.rsoi.cicd.api.v1.model.ErrorResponse;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class PersonsExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalException(Exception e, WebRequest request) {
        var errorResponse = new ErrorResponse()
                .setMessage(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .setDescription(e.getMessage());
        return status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
