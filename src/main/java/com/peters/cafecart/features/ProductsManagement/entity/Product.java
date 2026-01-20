package com.peters.cafecart.features.ProductsManagement.entity;

import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products", indexes = {
    //Required to get categories by vendor shop id
    @Index(name = "idx_vendor_category", columnList = "vendor_id, category_id"),
    @Index(name = "idx_vendor_stock_tracked", columnList = "vendor_id, is_stock_tracked"),
})

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductOption> productOptionList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAdditionGroup> productAdditionGroups = new ArrayList<>();

    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "is_stock_tracked")
    private Boolean isStockTracked;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addProductOptions(List<ProductOption> options) {
        options.forEach(option -> {
            productOptionList.add(option);
            option.setProduct(this);
        });
    }

    public void updateProductOptions(List<ProductOption> options) {
       this.getProductOptionList().clear();
       addProductOptions(options);
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
