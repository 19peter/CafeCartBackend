package com.peters.cafecart.features.VendorManagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.peters.cafecart.features.DeliveryManagment.entity.DeliverySettings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vendor_shops", indexes = {
    @Index(name = "idx_vendor_id", columnList = "vendor_id"),
    @Index(name = "idx_vendor_email", columnList = "email"),
    @Index(name = "idx_vendor_id_name_address", columnList = "vendor_id, name, address, phone_number"),
    @Index(name = "idx_latitude_longitude_city", columnList = "latitude, longitude, city")
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

    @Column(name = "latitude", nullable = false)
    private Double latitude ;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;
    
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;
    
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

    @Column(name = "is_online_payment_available", nullable = false)
    private boolean isOnlinePaymentAvailable;

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
