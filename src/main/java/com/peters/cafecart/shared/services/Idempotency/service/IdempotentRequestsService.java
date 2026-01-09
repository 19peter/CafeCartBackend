package com.peters.cafecart.shared.services.Idempotency.service;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.shared.services.Idempotency.entity.IdempotentRequest;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
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

    public IdempotentRequest begin(Long userId, String key, String requestHash) {
        try {
            IdempotentRequest req = new IdempotentRequest();
            req.setUserId(userId);
            req.setIdempotencyKey(key);
            req.setRequestHash(requestHash);
            req.setStatus(IdempotentRequest.IdempotencyStatus.PENDING);
            return idempotentRequestRepository.save(req);
        } catch (DataIntegrityViolationException e) {
            // Unique constraint hit — load existing
            IdempotentRequest existing = idempotentRequestRepository.findByIdempotencyKeyAndUserId(key, userId)
                    .orElseThrow(() -> new IllegalStateException("Idempotent request exists but cannot load"));
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new ValidationException("Idempotency key reused with different request body");
            }
            return existing;
        }
    }

    public boolean isCompleted(IdempotentRequest req) {
        return req.getStatus() == IdempotentRequest.IdempotencyStatus.COMPLETED;
    }

    public <T> T getStoredResponse(IdempotentRequest req, Class<T> clazz) {
        try {
            return objectMapper.readValue(req.getResponseJson(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize stored idempotent response", e);
        }
    }

    public void complete(IdempotentRequest req, Object responsePayload) {
        try {
            req.setResponseJson(objectMapper.writeValueAsString(responsePayload));
            req.setStatus(IdempotentRequest.IdempotencyStatus.COMPLETED);
            idempotentRequestRepository.save(req);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store idempotent response", e);
        }
    }

    public void fail(IdempotentRequest req, String message) {
        req.setStatus(IdempotentRequest.IdempotencyStatus.FAILED);
        idempotentRequestRepository.save(req);
    }


}
