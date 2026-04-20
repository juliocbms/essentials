package com.mysaas.essentials.controllers.Secrets;

import com.mysaas.essentials.controllers.docs.Secrets.SecretAdminControllerDocs;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/manage/secret")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SecretAdminController implements SecretAdminControllerDocs {

    private final SecretService secretService;

    public SecretAdminController(SecretService secretService) {
        this.secretService = secretService;
    }

    @GetMapping("/allSecrets")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<SecretResponse>>> getAllSecrets(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                             @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                             @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                             @RequestParam(value = "status", required = false) Boolean status,
                                                                             PagedResourcesAssembler<Secret> pagedResourcesAssembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"name"));
        PagedModel<EntityModel<SecretResponse>> response =
                secretService.getAllSecrets(pageable, pagedResourcesAssembler);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public ResponseEntity<EntityModel<SecretResponse>> getSecretById(@PathVariable UUID id) {
        EntityModel<SecretResponse> response = secretService.getSecretById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EntityModel<SecretResponse>> updateSecretById(@PathVariable UUID id, @Valid @RequestBody UpdateSecretRequest request){
        EntityModel<SecretResponse> response = secretService.updateSecret(request,id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSecretById(@PathVariable UUID id){
        secretService.deleteSecret(id);
        return ResponseEntity.noContent().build();
    }
}
