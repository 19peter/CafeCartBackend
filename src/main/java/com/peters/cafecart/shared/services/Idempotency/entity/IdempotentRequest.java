package com.peters.cafecart.shared.services.Idempotency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "idempotent_requests",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"idempotency_key", "user_id"}
        )
)
public class IdempotentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idempotencyKey;

    private Long userId;

    private String requestHash;

    private LocalDateTime createdAt;
}
