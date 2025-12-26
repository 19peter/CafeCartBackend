package com.peters.cafecart.shared.services.Idempotency.service;

import com.peters.cafecart.shared.services.Idempotency.entity.IdempotentRequest;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peters.cafecart.shared.services.Idempotency.repository.IdempotentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdempotentRequestsService {
    @Autowired IdempotentRequestRepository idempotentRequestRepository;
    @Autowired ObjectMapper objectMapper;

    public Optional<IdempotentRequest> getIdempotentRequestByUserIdAndIdempotencyKey(Long userId, String key) {
        return idempotentRequestRepository.findByIdempotencyKeyAndUserId(key, userId);
    }

    public String hashRequest(Object request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            // Use md5DigestAsHex instead of md5Digest
            return DigestUtils.md5DigestAsHex(json.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException("Failed to hash request", e);
        }
    }

    public void saveRequest(String key, Long userId, String hash) {
        IdempotentRequest idempotentRequest = new IdempotentRequest();
        idempotentRequest.setCreatedAt(LocalDateTime.now());
        idempotentRequest.setRequestHash(hash);
        idempotentRequest.setUserId(userId);
        idempotentRequest.setIdempotencyKey(key);
        idempotentRequestRepository.save(idempotentRequest);
    }


    @Scheduled(cron = "0 0 * * * *")  // Runs every hour
    @Transactional
    public void deleteOldRecords() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            int deletedCount = idempotentRequestRepository.deleteByCreatedAtBefore(cutoffTime);
        } catch (Exception e) {

        }
    }
}
