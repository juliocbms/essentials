package com.mysaas.essentials.Schedule;

import com.mysaas.essentials.services.Secret.SecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SecretRotationScheduler {

    private final SecretService secretService;
    private final Logger logger = LoggerFactory.getLogger(SecretRotationScheduler.class.getName());

    public SecretRotationScheduler(SecretService secretService) {
        this.secretService = secretService;
    }

    @Scheduled(fixedDelay = 60000)
    public void runRotation() {
        int rotatedCount = secretService.rotateBatch(100); // Lote de 100
        if (rotatedCount > 0) {
            logger.info("Rotação em background: {} segredos migrados.", rotatedCount);
        }
    }
}
