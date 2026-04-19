package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.config.JWTUserData;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.ChangePasswordRequest;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserHelper userHelper;

    @Mock
    private UserModelAssembler userModelAssembler;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User user;
    private UserRegisterResponse response;
    private UserRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("julio@email.com");
        user.setPasswordHash("hash_antigo");

        response = new UserRegisterResponse("Julio", "julio@email.com", LocalDateTime.now(), true);
        registerRequest = new UserRegisterRequest("Julio", "julio", "julio@email.com", "123456");
    }


    @Test
    void insertUser_ShouldReturnSuccess() {
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_senha");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(EntityModel.of(response));

        var result = authService.insertUser(registerRequest);

        assertNotNull(result);
        verify(userValidator).isEmailAndUsernameValidForInsert(registerRequest.email(), registerRequest.username());
        verify(userHelper).addDefaultRole(user);
        verify(userRepository).save(user);
    }

    @Test
    void insertUser_ShouldThrowEmailAlreadyExists_WhenDataIntegrityViolation() {
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.insertUser(registerRequest));
    }


    @Test
    void changePassword_ShouldWork_WithJWTUserData() {
        ChangePasswordRequest request = new ChangePasswordRequest("123456", "nova654", "nova654");
        JWTUserData userData = new JWTUserData("uuid-string", "julio@email.com", Collections.emptyList());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userData);

        when(userRepository.findByEmail("julio@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("nova654")).thenReturn("novo_hash");

        assertDoesNotThrow(() -> authService.changePassword(request));
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordsDoNotMatch() {
        ChangePasswordRequest request = new ChangePasswordRequest("123", "nova1", "nova2");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("julio@email.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(request));
        assertEquals("As senhas não coincidem.", ex.getMessage());
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("senha_errada", "nova", "nova");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("julio@email.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha_errada", user.getPassword())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(request));
        assertEquals("Senha antiga incorreta.", ex.getMessage());
    }

    @Test
    void changePassword_ShouldThrowNotFound_WhenUserDoesNotExist() {
        ChangePasswordRequest request = new ChangePasswordRequest("123", "nova", "nova");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("inexistente@email.com");
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.changePassword(request));
    }
}