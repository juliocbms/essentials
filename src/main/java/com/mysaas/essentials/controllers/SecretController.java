package com.mysaas.essentials.controllers;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;

import com.mysaas.essentials.services.Secret.SecretService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/secret")
public class SecretController {


    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @PostMapping("/create")
    public ResponseEntity<EntityModel<SecretResponse>> insertSecret(@Valid @RequestBody CreateSecretRequest request){
       EntityModel<SecretResponse> response = secretService.saveNewSecret(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> getSecretById(@PathVariable UUID id) {
        EntityModel<SecretResponse> response = secretService.getSecretById(id);
        return ResponseEntity.ok(response);
    }
}
