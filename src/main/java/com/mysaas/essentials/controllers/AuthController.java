package com.mysaas.essentials.controllers;

import com.mysaas.essentials.config.TokenConfig;
import com.mysaas.essentials.controllers.docs.AuthControllerDocs;
import com.mysaas.essentials.model.dto.UsersDTOS.Login.LoginRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Login.LoginResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.ChangePasswordRequest;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.services.Users.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@Tag(name = "Auth",description = "Endpoints for Auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController( AuthService authService, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }


    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> insertUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        EntityModel<UserRegisterResponse> response = authService.insertUser(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/user/logout")
    @Override
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/user/change-password")
    @Override
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

}
