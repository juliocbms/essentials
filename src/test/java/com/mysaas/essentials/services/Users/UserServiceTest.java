package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.mocks.MockUser;
import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

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

    @Mock
    private PagedResourcesAssembler<User> pagedResourcesAssembler;

    private MockUser input;
    private User user;
    private UserResponse response;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        input = new MockUser();
        uuid = UUID.randomUUID();
        user = input.mockEntity(1);
        user.setId(uuid);
        response = new UserResponse(uuid, "User Test 1", "user@test.com", "user1", true, false, LocalDateTime.now(), LocalDateTime.now(), null, java.util.Set.of("ROLE_USER"));
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        PagedModel<EntityModel<UserResponse>> pagedModel = PagedModel.of(
                List.of(EntityModel.of(response)),
                new PagedModel.PageMetadata(10, 0, 1)
        );

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        when(pagedResourcesAssembler.toModel(eq(userPage), any(UserModelAssembler.class)))
                .thenReturn(pagedModel);

        var result = userService.getAllUsers(pageable, pagedResourcesAssembler);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
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
        UpdateUserRequest request = mock(UpdateUserRequest.class);
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
        UpdateUserRequest request = mock(UpdateUserRequest.class);
        when(userHelper.findEntityOrThrow(uuid)).thenThrow(new ResourceNotFoundException(uuid));
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(request, uuid));
    }

    @Test
    void updateUser_ShouldThrowUsernameException() {
        UpdateUserRequest request = mock(UpdateUserRequest.class);
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
