package com.mysaas.essentials.controllers.Users;

import com.mysaas.essentials.controllers.docs.Users.UserControllerDocs;
import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.services.Users.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
@Tag(name = "Users",description = "Endpoints for Managing People")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<EntityModel<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @PatchMapping("/me")
    @Override
    public ResponseEntity<EntityModel<UserResponse>>  updateMyUserProfile(@Valid @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateAuthenticatedUser(request));
    }
}
