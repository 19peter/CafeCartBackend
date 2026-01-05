package com.peters.cafecart.features.ShopManagement.entity;

import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import com.peters.cafecart.features.VerifiedCustomerManagement.entity.VerifiedCustomer;
import com.peters.cafecart.shared.interfaces.Authenticatable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.peters.cafecart.features.DeliveryManagment.entity.DeliverySettings;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

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
public class VendorShop implements Authenticatable {
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

    @Column(name = "latitude")
    private Double latitude ;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "vendorShop", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliverySettings deliverySettings;

    @OneToMany(mappedBy = "vendorShop",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryAreas> deliveryAreasList;

    @Column(name = "is_online_payment_available", nullable = false)
    private boolean isOnlinePaymentAvailable;

    @ManyToMany
    @JoinTable(
            name = "vendor_blocked_customers",
            joinColumns = @JoinColumn(name = "vendor_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<Customer> blockedCustomers = new HashSet<>();

    @OneToMany(mappedBy = "lastUpdatedBy")
    private List<VerifiedCustomer> verifiedCustomers;


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
