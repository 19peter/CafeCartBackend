package com.peters.cafecart.features.ProductsManagement.service;

import java.util.Optional;

import com.peters.cafecart.features.ProductsManagement.entity.Product;

public interface ProductService {
    Optional<Product> getProductById(Long id);
}
