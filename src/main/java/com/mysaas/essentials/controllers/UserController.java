package com.mysaas.essentials.controllers;

import com.mysaas.essentials.controllers.docs.UserControllerDocs;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.UserUpdateRequest;
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
    public ResponseEntity<EntityModel<UserRegisterResponse>> getMyProfile() {
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @PatchMapping("/me")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>>  updateMyUserProfile(@Valid @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.updateAuthenticatedUser(request));
    }
}