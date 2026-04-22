package com.mysaas.essentials.controllers.Secrets;

import com.mysaas.essentials.controllers.docs.Secrets.SecretControllerDocs;
import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.services.Secret.SecretService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/secret")
@PreAuthorize("isAuthenticated()")
public class SecretController implements SecretControllerDocs {


    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> insertSecret(@Valid @RequestBody CreateSecretRequest request){
       EntityModel<SecretResponse> response = secretService.saveNewSecret(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<PagedModel<EntityModel<SecretResponse>>> getMySecrets(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                                @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                                @RequestParam(value = "status", required = false) Boolean status,
                                                                                PagedResourcesAssembler<Secret> pagedResourcesAssembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"secretName"));
        PagedModel<EntityModel<SecretResponse>> response =
                secretService.getAllSecrets(pageable, pagedResourcesAssembler);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/{id}")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> getMySecretById(@PathVariable UUID id) {
        EntityModel<SecretResponse> response = secretService.getSecretById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/{id}")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> updateMySecretById(@PathVariable UUID id, @Valid @RequestBody UpdateSecretRequest request){
        EntityModel<SecretResponse> response = secretService.updateSecret(request,id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/disable/{id}")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> deactivateMySecretById(@PathVariable UUID id){
        EntityModel<SecretResponse> response = secretService.desactiveSecret(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/active/{id}")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> activateMySecretById(@PathVariable UUID id){
        EntityModel<SecretResponse> response = secretService.restoreSecret(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/allSecrets")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<SecretResponse>>> getAllSecrets(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                 @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                                 @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                                 @RequestParam(value = "status", required = false) Boolean status,
                                                                                 PagedResourcesAssembler<Secret> pagedResourcesAssembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"secretName"));
        PagedModel<EntityModel<SecretResponse>> response =
                secretService.getAllSecrets(pageable, pagedResourcesAssembler);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/{id}")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> getSecretById(@PathVariable UUID id) {
        EntityModel<SecretResponse> response = secretService.getSecretById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{id}")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> updateSecretById(@PathVariable UUID id, @Valid @RequestBody UpdateSecretRequest request){
        EntityModel<SecretResponse> response = secretService.updateSecret(request,id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/disable/{id}")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> deactivateSecretById(@PathVariable UUID id){
        EntityModel<SecretResponse> response = secretService.desactiveSecret(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/active/{id}")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> activeSecretById(@PathVariable UUID id){
        EntityModel<SecretResponse> response = secretService.restoreSecret(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{id}")
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteSecretById(@PathVariable UUID id){
        secretService.deleteSecret(id);
        return ResponseEntity.noContent().build();
    }
}
