package com.peters.cafecart.features.AdditionsManagement.entity;

import com.peters.cafecart.features.ProductsManagement.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_addition_groups", uniqueConstraints = {
    @UniqueConstraint(name = "uk_product_group", columnNames = "product_id, addition_group_id")
})
public class ProductAdditionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addition_group_id", nullable = false)
    private AdditionGroup additionGroup;
}
