package com.mysaas.essentials.controllers.exceptions;

import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.RegraNegocioException;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<StandardError> emailAlreadyExists(EmailAlreadyExistsException e, HttpServletRequest request){
        String error = "Email Already Exists";
        HttpStatus status =HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(),status.value(),error,e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<StandardError> emailAlreadyExists(UsernameAlreadyExistsException e, HttpServletRequest request){
        String error = "Username Already Exists";
        HttpStatus status =HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(),status.value(),error,e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        String error = "Resource Not Found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(),status.value(),error,e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> regraNegocio(RegraNegocioException e, HttpServletRequest request){
        String error = "Business rules error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(),status.value(),error,e.getMessage(),request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Validation Error";
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;


        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro in the fields");

        StandardError err = new StandardError(Instant.now(), status.value(), error, errorMessage, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}