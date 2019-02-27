package com.incuube.agent.controllers.advices;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.api.client.http.HttpResponseException;
import com.incuube.agent.controllers.RcsController;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseItemNotFound;
import com.incuube.rcs.datamodel.exceptions.RbmNotSupportingException;
import com.incuube.rcs.datamodel.util.ErrorCodes;
import com.incuube.rcs.datamodel.util.RestValidationAnswer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//TODO(igordiachenko): Refactor RbmConnectionException
//TODO(igordiachenko): ADD LOGGING
@ControllerAdvice(basePackageClasses = RcsController.class)
@Log4j2
public class RcsControllerAdvice {

    @ExceptionHandler(RbmNotSupportingException.class)
    public ResponseEntity<String> handleRbmNotSupportingException(RbmNotSupportingException supportingException) {
        return ResponseEntity.status(supportingException.getCode()).body(supportingException.getMessage());
    }

    @ExceptionHandler(RbmConnectionException.class)
    public ResponseEntity<String> handleRbmConnectionException(RbmConnectionException connectionException) {
        Throwable exceptionCause = connectionException.getCause();
        if (exceptionCause instanceof HttpResponseException) {
            HttpResponseException googleRequestException = (HttpResponseException) exceptionCause;
            return ResponseEntity.status(googleRequestException.getStatusCode()).body(googleRequestException.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(connectionException.getMessage());
    }

    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<String> handleMismatchedInputException(MismatchedInputException jacksonConverteException) {
        StringBuilder sb = new StringBuilder(jacksonConverteException.getMessage());
        int i = sb.indexOf("(");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jacksonConverteException.getMessage().substring(0, i));
    }

    @ExceptionHandler(RbmDatabaseException.class)
    public ResponseEntity<String> handleRbmDatabaseException(RbmDatabaseException databaseException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(databaseException.getMessage());
    }

    @ExceptionHandler(RbmDatabaseItemNotFound.class)
    public ResponseEntity<String> handleRbmDatabaseItemNotFoundException(RbmDatabaseItemNotFound itemNotFound) {
        log.info(String.format("Item '%s' with name '%s' not found in database.", itemNotFound.getItemType(), itemNotFound.getItemName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item '" + itemNotFound.getItemType() + "' with name '" + itemNotFound.getItemName() + "' not found in database.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestValidationAnswer> handle(MethodArgumentNotValidException ex) {
        RestValidationAnswer answer = new RestValidationAnswer();

        log.error("Validation exception. " + ex.getMessage());

        StringBuilder message = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(error -> message.append("Invalid value submitted for '").append(error.getField()).append("' field - ")
                .append(error.getDefaultMessage()).append("; "));

        answer.setCode(ErrorCodes.ValidationError.getErrorCode());
        answer.setErrorMessages(message.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answer);
    }

}
