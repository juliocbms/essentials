package com.mysaas.essentials.controllers.exceptions;

import com.mysaas.essentials.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<StandardError> emailAlreadyExists(EmailAlreadyExistsException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Email Already Exists", e.getMessage(), request);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<StandardError> emailAlreadyExists(UsernameAlreadyExistsException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Username Already Exists", e.getMessage(), request);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<StandardError> invalidPassword(InvalidPasswordException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Invalid Password", e.getMessage(), request);
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<StandardError> PasswordDONotMAtchException(PasswordsDoNotMatchException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Password do not Match", e.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> badCredentialsExceptionException(BadCredentialsException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Bad Credentials Exception", e.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", e.getMessage(), request);
    }

    @ExceptionHandler(RoleNotFoundedExcpetion.class)
    public ResponseEntity<StandardError> roleNotFound(RoleNotFoundedExcpetion e, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", e.getMessage(), request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> regraNegocio(RegraNegocioException e, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST, "Business rules error", e.getMessage(), request);
    }

//    @ExceptionHandler(UnAtorizedeException.class)
//    public ResponseEntity<StandardError> semAcesso(UnAtorizedeException e, HttpServletRequest request){
//        String error = "UnAutorized Exception";
//        HttpStatus status = HttpStatus.FORBIDDEN;
//        StandardError err = new StandardError(Instant.now(),status.value(),error,e.getMessage(),request.getRequestURI());
//        return ResponseEntity.status(status).body(err);
//    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                "You do not have permission to access this resource.",
                request
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> userNotFound(UserNotFoundException e, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, "User Not Founded", e.getMessage(), request);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro in the fields");

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error", errorMessage, request);
    }

    @ExceptionHandler(SecretAlreadyExistsException.class)
    public ResponseEntity<StandardError> secretAlreadyExists(SecretAlreadyExistsException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, "Secret Already Exists", e.getMessage(), request);
    }

    @ExceptionHandler(SecretAlreadyActiveException.class)
    public ResponseEntity<StandardError> secretAlreadyActive(SecretAlreadyActiveException e, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST, "Secret Already Active", e.getMessage(), request);
    }

    @ExceptionHandler(VaultKeyNotFoundException.class)
    public ResponseEntity<StandardError> vaultKeyNotFound(VaultKeyNotFoundException e, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, "Vault Key Not Found", e.getMessage(), request);
    }

    @ExceptionHandler(SecretEncryptionException.class)
    public ResponseEntity<StandardError> secretEncryption(SecretEncryptionException e, HttpServletRequest request){
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Encryption Error", e.getMessage(), request);
    }

}
