package com.peters.cafecart.features.AdditionsManagement.entity;

import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shop_additions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_shop_addition", columnNames = "shop_id, addition_id")
})
public class ShopAddition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private VendorShop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addition_id", nullable = false)
    private Addition addition;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
