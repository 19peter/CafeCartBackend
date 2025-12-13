package com.peters.cafecart.features.ProductsManagement.service;

import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.ProductsManagement.dto.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.mapper.CategoryMapper;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryMapper categoryMapper;

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId) {
        return categoryMapper.toDtoList(productRepository.findCategoriesByShopId(vendorShopId));
    }

    @Override
    public boolean isStockTracked(Long productId) {
        return productRepository.findById(productId).map(Product::getIsStockTracked).orElse(false);
    }
}
