package com.peters.cafecart.features.ProductsManagement.service;


import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.ProductsManagement.dto.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;

public interface ProductService {
    Optional<Product> getProductById(Long id);

    List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId);

    boolean isStockTracked(Long productId);
}
