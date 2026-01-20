package com.peters.cafecart.features.ProductsManagement.service;


import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddCategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;

public interface ProductService {
    Optional<Product> getProductById(Long id);

    List<ProductDto> getProductsForVendorShopByVendorId(Long vendorId);

    List<CategoryDto> getCategories();

    CategoryDto addCategory(AddCategoryDto categoryDto);

    List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId);

    boolean isStockTracked(Long productId);

    AddProductResponseDto addProduct(AddProductRequestDto productDto,
                                     Long vendorId,
                                     List<ProductOption> options,
                                     List<AdditionGroup> additionGroups);

    UpdateProductResponseDto updateProduct(UpdateProductRequestDto updateProductDto,
                                           Product product,
                                           List<AdditionGroup> additionGroups);

    boolean saveProductImage(Long productId, String imageUrl);
}
