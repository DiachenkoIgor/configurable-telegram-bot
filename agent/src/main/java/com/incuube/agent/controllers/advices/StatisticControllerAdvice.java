package com.incuube.agent.controllers.advices;


import com.incuube.agent.controllers.StatisticController;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = StatisticController.class)
public class StatisticControllerAdvice {
    @ExceptionHandler(RbmConnectionException.class)
    public ResponseEntity<String> handleRbmConnectionException(RbmConnectionException connectionException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(connectionException.getMessage());
    }
}
