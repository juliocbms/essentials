package com.mysaas.essentials.controllers.exceptions;

import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.SecretAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final ResourceExceptionHandler handler = new ResourceExceptionHandler();

    @Test
    void resourceNotFound_ShouldReturn404() {
        when(request.getRequestURI()).thenReturn("/users/admin/id");

        ResponseEntity<StandardError> response =
                handler.resourceNotFound(new ResourceNotFoundException("not found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource Not Found", response.getBody().getError());
        assertEquals("not found", response.getBody().getMessage());
    }

    @Test
    void secretAlreadyExists_ShouldReturn409() {
        when(request.getRequestURI()).thenReturn("/secret/create");

        ResponseEntity<StandardError> response =
                handler.secretAlreadyExists(new SecretAlreadyExistsException("duplicado"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Secret Already Exists", response.getBody().getError());
        assertEquals("duplicado", response.getBody().getMessage());
        assertEquals("/secret/create", response.getBody().getPath());
    }

    @Test
    void accessDenied_ShouldReturn403WithDefaultMessage() {
        when(request.getRequestURI()).thenReturn("/users/admin/allusers");

        ResponseEntity<StandardError> response =
                handler.accessDenied(new AccessDeniedException("forbidden"), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied", response.getBody().getError());
        assertEquals("You do not have permission to access this resource.", response.getBody().getMessage());
    }

    @Test
    void validation_ShouldReturn422WithFirstFieldMessage() throws NoSuchMethodException {
        when(request.getRequestURI()).thenReturn("/auth/register");

        DummyValidationTarget target = new DummyValidationTarget();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "email", "Email is mandatory"));

        Method method = DummyValidationTarget.class.getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<StandardError> response = handler.validation(ex, request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Error", response.getBody().getError());
        assertEquals("Email is mandatory", response.getBody().getMessage());
    }

    private static class DummyValidationTarget {
        @SuppressWarnings("unused")
        public void dummyMethod(String value) {
        }
    }
}
