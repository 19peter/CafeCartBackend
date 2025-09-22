package com.peters.cafecart.features.VendorManagement.entity;

import com.peters.cafecart.features.DeliveryManagement.entity.DeliverySettings;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Time;
import java.time.LocalDateTime;



@Data
@Entity
@Table(name = "vendor_shops", indexes = {
    @Index(name = "idx_vendor_id", columnList = "vendor_id"),
    @Index(name = "idx_vendor_id_name_address", columnList = "vendor_id, name, address, phone_number")
})
public class VendorShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    
    @Column(nullable = false)
    private String address;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "opening_time")
    private Time openingTime;
    
    @Column(name = "closing_time")
    private Time closingTime;

    @Column(name = "last_order_time")
    private Time lastOrderTime;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "vendorShop", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliverySettings deliverySettings;

    @Column(name = "is_delivery_available", nullable = false)
    private boolean isDeliveryAvailable;
    
    @Transient
    private Boolean isOpen;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Boolean getIsOpen() {
        LocalDateTime now = LocalDateTime.now();
        return openingTime.toLocalTime().isBefore(now.toLocalTime()) && closingTime.toLocalTime().isAfter(now.toLocalTime());
    }
}
