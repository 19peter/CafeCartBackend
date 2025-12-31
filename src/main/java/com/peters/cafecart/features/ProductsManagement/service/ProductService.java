package com.peters.cafecart.features.ProductsManagement.service;


import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.ProductsManagement.dto.request.AddCategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;

public interface ProductService {
    Optional<Product> getProductById(Long id);

    List<ProductDto> getProductsForVendorShopByVendorId(Long vendorId);

    List<CategoryDto> getCategories();

    CategoryDto addCategory(AddCategoryDto categoryDto);

    List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId);

    boolean isStockTracked(Long productId);

    AddProductResponseDto addProduct(AddProductRequestDto productDto, Long vendorId);

    UpdateProductResponseDto updateProduct(UpdateProductRequestDto updateProductDto, Long vendorId);

    boolean saveProductImage(Long productId, String imageUrl);
}
