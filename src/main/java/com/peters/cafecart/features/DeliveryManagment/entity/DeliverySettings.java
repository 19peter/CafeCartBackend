package com.peters.cafecart.features.DeliveryManagment.entity;

import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "delivery_settings", indexes = {
    @Index(columnList = "vendor_shop_id, base_fee, rate_per_km")
})
public class DeliverySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_shop_id", nullable = false)
    private VendorShop vendorShop;
    
    @Column(name = "base_fee", nullable = false)
    private double baseFee;
    
    @Column(name = "rate_per_km", nullable = false)
    private double ratePerKm;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivery_approach")
    @Enumerated(EnumType.STRING)
    DeliverySettingsEnum deliveryApproach;

    @Column(name = "is_delivery_available")
    private Boolean isDeliveryAvailable;

    
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
