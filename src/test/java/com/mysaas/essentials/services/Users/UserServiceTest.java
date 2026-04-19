package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.mocks.MockUser;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.UserUpdateRequest;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserHelper userHelper;

    @Mock
    private UserModelAssembler userModelAssembler;

    private MockUser input;
    private User user;
    private UserRegisterResponse response;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        input = new MockUser();
        uuid = UUID.randomUUID();
        user = input.mockEntity(1);
        user.setId(uuid);
        response = new UserRegisterResponse("User Test 1", "user@test.com", LocalDateTime.now(), true);
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        User user2 = input.mockEntity(2);
        List<User> users = List.of(user, user2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        when(userModelAssembler.toModel(any(User.class)))
                .thenReturn(EntityModel.of(response));

        var result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        verify(userRepository).findAll(any(Pageable.class));
        verify(userModelAssembler, times(2)).toModel(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnSuccess() {
        var linkSelf = org.springframework.hateoas.Link.of("/users/" + uuid).withSelfRel();
        var modelComLink = EntityModel.of(response).add(linkSelf);
        when(userHelper.findEntityOrThrow(uuid)).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(modelComLink);
        var result = userService.getUserById(uuid);
        assertNotNull(result);
        assertTrue(result.hasLink("self"));
    }

    @Test
    void getUserById_ShouldThrowNotFound() {
        when(userHelper.findEntityOrThrow(uuid)).thenThrow(new ResourceNotFoundException(uuid));
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(uuid));
    }

    @Test
    void updateUser_ShouldReturnSuccess() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("user");
        when(userHelper.findEntityOrThrow(uuid)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userModelAssembler.toModel(user)).thenReturn(EntityModel.of(response));
        var result = userService.updateUser(request, uuid);
        assertNotNull(result);
        verify(userValidator).isUsernameValidForUpdate("user", uuid);
    }

    @Test
    void updateUser_ShouldThrowNotFound() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(userHelper.findEntityOrThrow(uuid)).thenThrow(new ResourceNotFoundException(uuid));
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(request, uuid));
    }

    @Test
    void updateUser_ShouldThrowUsernameException() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("user_error");
        when(userHelper.findEntityOrThrow(uuid)).thenReturn(user);
        doThrow(new UsernameAlreadyExistsException("user_error"))
                .when(userValidator).isUsernameValidForUpdate("user_error", uuid);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.updateUser(request, uuid));
    }

    @Test
    void deleteUser_ShouldWork() {
        when(userHelper.findEntityOrThrow(uuid)).thenReturn(user);
        assertDoesNotThrow(() -> userService.deleteUser(uuid));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowNotFound() {
        when(userHelper.findEntityOrThrow(uuid)).thenThrow(new ResourceNotFoundException(uuid));
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(uuid));
    }
}