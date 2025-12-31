package com.peters.cafecart.features.CustomerManagement.entity;

import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.VerifiedCustomerManagement.entity.VerifiedCustomer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_phone_number", columnList = "phone_number, first_name"),
    @Index(name = "idx_customer_email", columnList = "email")
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Size(min = 2, max = 15)
    private String firstName;
    
    @Column(nullable = false)
    @Size(min = 2, max = 15)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate dob;
    
    @Column(name = "phone_number", unique = true, nullable = false)
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^(010|011|012|015)\\d{8}$")
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<VerifiedCustomer> verifiedVendors;

    @Column(name = "is_phone_verified")
    private Boolean isPhoneVerified;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @Column(name = "saved_address")
    private String address;


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
