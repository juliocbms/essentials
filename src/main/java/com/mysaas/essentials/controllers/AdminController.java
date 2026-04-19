package com.mysaas.essentials.controllers;

import com.mysaas.essentials.controllers.docs.AdminControllerDocs;
import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserRoleRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserStatusRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.services.Users.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin",description = "Endpoints for Admins")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController implements AdminControllerDocs {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allusers")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UserResponse>>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                     @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                                     @RequestParam(value = "status", required = false) Boolean status,
                                                                                     PagedResourcesAssembler<User> pagedResourcesAssembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"name"));
        PagedModel<EntityModel<UserResponse>> response =
                userService.getAllUsers(pageable, pagedResourcesAssembler);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findUsersByName/{name}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UserResponse>>> findByName(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                     @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                                     @RequestParam(value = "status", required = false) Boolean status,
                                                                                     @PathVariable("name") String name,
                                                                                     PagedResourcesAssembler<User> pagedResourcesAssembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"name"));
        PagedModel<EntityModel<UserResponse>> response =
                userService.findByName(name,pageable, pagedResourcesAssembler);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(@PathVariable UUID id) {
        EntityModel<UserResponse> response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EntityModel<UserResponse>> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        EntityModel<UserResponse> response = userService.updateUser(request, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/role/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EntityModel<UserResponse>> updateRoleByUserID(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleRequest request){
        EntityModel<UserResponse> response = userService.updateRoleByUser(request, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  ResponseEntity<EntityModel<UserResponse>> updateStatusByUserId(@PathVariable UUID id, @Valid @RequestBody UpdateUserStatusRequest request){
        EntityModel<UserResponse> response = userService.updateStatusByUser(request, id);
        return ResponseEntity.ok(response);
    }


}
