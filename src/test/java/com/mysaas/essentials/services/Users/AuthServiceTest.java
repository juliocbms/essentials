package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.model.dto.auth.ChangePasswordRequest;
import com.mysaas.essentials.model.dto.user.CreateUserRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.InvalidPasswordException;
import com.mysaas.essentials.services.exceptions.PasswordsDoNotMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private User user;
    private UserResponse response;
    private CreateUserRequest registerRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("julio@email.com");
        user.setPasswordHash("hash_antigo");

        response = new UserResponse(
                UUID.randomUUID(),
                "Julio",
                "julio@email.com",
                "julio",
                true,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                Collections.singleton("ROLE_USER")
        );
        registerRequest = new CreateUserRequest("Julio", "julio@email.com", "julio", "123456");
    }

    @Test
    void insertUser_ShouldReturnSuccess() {
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("hash_senha");
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
        when(passwordEncoder.encode(any())).thenReturn("hash_senha");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.insertUser(registerRequest));
    }

    @Test
    void changePassword_ShouldWork_WhenRequestIsValid() {
        ChangePasswordRequest request = new ChangePasswordRequest("123456", "nova654", "nova654");

        when(userHelper.getAuthenticatedUserEntity()).thenReturn(user);
        when(passwordEncoder.matches("123456", "hash_antigo")).thenReturn(true);
        when(passwordEncoder.encode("nova654")).thenReturn("novo_hash");

        assertDoesNotThrow(() -> authService.changePassword(request));
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordsDoNotMatch() {
        ChangePasswordRequest request = new ChangePasswordRequest("123", "nova1", "nova2");

        when(userHelper.getAuthenticatedUserEntity()).thenReturn(user);
        when(passwordEncoder.matches("123", "hash_antigo")).thenReturn(true);

        assertThrows(PasswordsDoNotMatchException.class, () -> authService.changePassword(request));
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("senha_errada", "nova", "nova");

        when(userHelper.getAuthenticatedUserEntity()).thenReturn(user);
        when(passwordEncoder.matches("senha_errada", "hash_antigo")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> authService.changePassword(request));
    }
}
