package com.peters.cafecart.shared.services.Idempotency.repository;

import com.peters.cafecart.shared.services.Idempotency.entity.IdempotentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IdempotentRequestRepository
        extends JpaRepository<IdempotentRequest, Long> {

    Optional<IdempotentRequest> findByIdempotencyKeyAndUserId(
            String idempotencyKey,
            Long userId
    );

    @Modifying
    @Query("DELETE FROM IdempotentRequest r WHERE r.createdAt < :cutoffTime")
    int deleteByCreatedAtBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}