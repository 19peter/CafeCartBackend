package com.peters.cafecart.features.ProductsManagement.service;

import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductOptionsService {


    List<ProductOption> createProductOptionsForProduct(ProductOptionInformationDto options);
    List<ProductOption> updateProductOption(ProductOptionInformationDto options);
    void deleteProductOption(ProductOptionDto options);
    Optional<ProductOption> getProductOption(Long id);
}
