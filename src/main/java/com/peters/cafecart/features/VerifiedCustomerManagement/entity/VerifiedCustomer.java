package com.peters.cafecart.features.VerifiedCustomerManagement.entity;

import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "verified_customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_id", "vendor_id"})
        },
        indexes = {
                @Index(name = "idx_customer_vendor", columnList = "customer_id, vendor_id")
        }
)
@Getter
@Setter
public class VerifiedCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "last_updated_by_shop_id")
    private VendorShop lastUpdatedBy;

    @Column(nullable = false)
    private LocalDateTime verifiedAt = LocalDateTime.now();
}
