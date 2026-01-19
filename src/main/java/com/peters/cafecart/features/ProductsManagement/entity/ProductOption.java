package com.peters.cafecart.features.ProductsManagement.entity;

import com.peters.cafecart.shared.enums.ProductSizes;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product_options", indexes = {
        @Index(name = "idx_product_id", columnList = "id, product_id")
})
@Data
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    ProductSizes size;

    @Column(name = "price", nullable = false)
    BigDecimal price;

}
