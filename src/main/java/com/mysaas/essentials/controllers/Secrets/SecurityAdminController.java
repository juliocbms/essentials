package com.mysaas.essentials.controllers.Secrets;

import com.mysaas.essentials.services.Secret.MasterKeyService;
import com.mysaas.essentials.services.Secret.SecretService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/security")
@PreAuthorize("hasAuthority('ADMIN')")
public class SecurityAdminController {

    private final SecretService secretService;
    private final MasterKeyService masterKeyService;

    public SecurityAdminController(SecretService secretService, MasterKeyService masterKeyService) {
        this.secretService = secretService;
        this.masterKeyService = masterKeyService;
    }

    @PostMapping("/master-keys/generate")
    public ResponseEntity<String> generateNewKey(@RequestParam Integer version) {
        masterKeyService.generateAndEncryptNewMasterKey(version);
        return ResponseEntity.ok("Nova Master Key V" + version + " gerada e salva no banco!");
    }

    @PostMapping("/secrets/rotate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Integer> rotateBatch(@RequestParam(defaultValue = "100") int batchSize) {
        int rotatedCount = secretService.rotateBatch(batchSize);
        return ResponseEntity.ok(rotatedCount);
    }
}
