package com.peters.cafecart.features.InventoryManagement.entity;

import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_shop_quantity", columnList = "vendor_shop_id, quantity"),
    @Index(name = "idx_shop_product_quantity", columnList = "vendor_shop_id, product_id, quantity"),

})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_shop_id", nullable = false)
    private VendorShop vendorShop;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PostUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
