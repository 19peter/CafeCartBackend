package com.peters.cafecart.features.AdditionsManagement.entity;

import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "addition_groups", indexes = {
    @Index(name = "idx_vendor_group_name", columnList = "vendor_id, name", unique = true)
})
public class AdditionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "additionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Addition> additions = new HashSet<>();

    @Column(nullable = false)
    private String name;

    @Column(name = "max_selectable", nullable = false)
    private Integer maxSelectable = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;


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
