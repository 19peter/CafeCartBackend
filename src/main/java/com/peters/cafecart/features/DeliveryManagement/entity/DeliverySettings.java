package com.peters.cafecart.features.DeliveryManagement.entity;

import com.peters.cafecart.features.VendorManagement.entity.VendorShop;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "delivery_settings")
public class DeliverySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_shop_id", nullable = false)
    private VendorShop vendorShop;
    
    @Column(name = "base_fee", nullable = false)
    private BigDecimal baseFee;
    
    @Column(name = "rate_per_km", nullable = false)
    private BigDecimal ratePerKm;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private boolean isDeliveryAvailable;

    @PostLoad
    protected void onPostLoad() {
        isDeliveryAvailable = vendorShop.isDeliveryAvailable();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
