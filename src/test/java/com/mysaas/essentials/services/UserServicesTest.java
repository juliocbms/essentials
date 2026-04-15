package com.mysaas.essentials.services;

import com.mysaas.essentials.mocks.MockUser;
import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.UserUpdateRequest;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    private MockUser input;

    @InjectMocks
    private UserServices services;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    private User user;
    private UserRegisterResponse response;
    private UUID uuid;
    private Role role;

    @BeforeEach
    void setUp() {
        input = new MockUser();
        uuid = UUID.randomUUID();

        user = input.mockEntity(1);
        user.setId(uuid);

        response = new UserRegisterResponse(
                "User Test 1",
                "user@test.com",
                LocalDateTime.now(),
                true
        );

        role = new Role();
        role.setName("ROLE_USER");
    }


    @Test
    void insertUser_ShouldReturnSuccess() {
        UserRegisterRequest request = mock(UserRegisterRequest.class);

        when(request.email()).thenReturn("user@test.com");
        when(request.username()).thenReturn("user");
        when(request.password()).thenReturn("123");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        var result = services.insertUser(request);

        assertNotNull(result);
        assertTrue(result.hasLink("self"));
    }

    @Test
    void insertUser_ShouldThrowEmailException() {
        UserRegisterRequest request = mock(UserRegisterRequest.class);

        when(request.email()).thenReturn("user@test.com");
        when(request.username()).thenReturn("user");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> services.insertUser(request));
    }

    @Test
    void insertUser_ShouldThrowUsernameException() {
        UserRegisterRequest request = mock(UserRegisterRequest.class);

        when(request.email()).thenReturn("user@test.com");
        when(request.username()).thenReturn("user");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> services.insertUser(request));
    }

    @Test
    void insertUser_ShouldThrowDataIntegrityException() {
        UserRegisterRequest request = mock(UserRegisterRequest.class);

        when(request.email()).thenReturn("user@test.com");
        when(request.username()).thenReturn("user");
        when(request.password()).thenReturn("123");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailAlreadyExistsException.class,
                () -> services.insertUser(request));
    }



    @Test
    void getUserById_ShouldReturnSuccess() {
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        var result = services.getUserById(uuid);

        assertNotNull(result);
        assertTrue(result.hasLink("self"));
    }

    @Test
    void getUserById_ShouldThrowNotFound() {
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> services.getUserById(uuid));
    }


    @Test
    void getAllUsers_ShouldReturnList() {
        User user2 = input.mockEntity(2);
        user2.setId(UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        when(userMapper.toResponse(any())).thenReturn(response);

        CollectionModel<EntityModel<UserRegisterResponse>> result =
                services.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }


    @Test
    void updateUser_ShouldReturnSuccess() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);

        when(request.username()).thenReturn("user");
        when(request.active()).thenReturn(true);

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot(any(), any())).thenReturn(false);

        when(userMapper.updateToEntity(request, user)).thenReturn(user);

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        var result = services.updateUser(request, uuid);

        assertNotNull(result);
    }

    @Test
    void updateUser_ShouldThrowNotFound() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);

        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> services.updateUser(request, uuid));
    }

    @Test
    void updateUser_ShouldThrowUsernameException() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);

        when(request.username()).thenReturn("user");

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot(any(), any())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> services.updateUser(request, uuid));
    }


    @Test
    void deleteUser_ShouldWork() {
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> services.deleteUser(uuid));

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowNotFound() {
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> services.deleteUser(uuid));
    }
}