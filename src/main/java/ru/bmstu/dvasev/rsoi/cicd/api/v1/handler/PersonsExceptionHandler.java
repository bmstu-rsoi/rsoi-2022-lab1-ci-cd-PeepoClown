package ru.bmstu.dvasev.rsoi.cicd.api.v1.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.bmstu.dvasev.rsoi.cicd.api.v1.model.ErrorResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestControllerAdvice
public class PersonsExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        log.error("Validation error: '{}'", e.getMessage(), e);
        var errorResponse = new ErrorResponse()
                .setMessage(BAD_REQUEST.getReasonPhrase())
                .setDescription(e.getAllErrors().toString());
        return status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(NumberFormatException e, WebRequest request) {
        log.error("Validation error: '{}'", e.getMessage(), e);
        var errorResponse = new ErrorResponse()
                .setMessage(BAD_REQUEST.getReasonPhrase())
                .setDescription(e.getMessage());
        return status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalException(Exception e, WebRequest request) {
        log.error("Internal error: '{}'", e.getMessage(), e);
        var errorResponse = new ErrorResponse()
                .setMessage(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .setDescription(e.getMessage());
        return status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
