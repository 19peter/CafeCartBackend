package com.peters.cafecart.features.ProductsManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
