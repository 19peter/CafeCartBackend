package com.peters.cafecart.features.PaymentManagement.entity;

import lombok.Getter;
import lombok.Setter;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Index;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_info", indexes = {
    @Index(columnList = "vendor_id, integration_id, private_key, public_key", name = "vendor_id_idx")
})
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "integration_id", nullable = false)
    private int integrationId;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "private_key", nullable = false)
    private String privateKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
