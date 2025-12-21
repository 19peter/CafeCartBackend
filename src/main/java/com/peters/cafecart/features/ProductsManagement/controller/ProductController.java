package com.peters.cafecart.features.ProductsManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.workflows.AddProductUseCase;

@RestController
@RequestMapping(Constants.API_V1 + "/products")
public class ProductController {
    @Autowired private ProductServiceImpl productService;
    @Autowired private AddProductUseCase addProductUseCase;
    
    @GetMapping("/vendor/{vendorShopId}/categories")
    public List<CategoryDto> getCategoriesByVendorShopId(
            @PathVariable Long vendorShopId) {
        return productService.getCategoriesByVendorShopId(vendorShopId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return productService.getCategories();
    }

    @PostMapping("/vendor/{vendorId}/add")
    public AddProductResponseDto addProduct(@PathVariable Long vendorId,@RequestBody AddProductRequestDto productDto) {
        return addProductUseCase.execute(productDto, vendorId);
    }

    @PostMapping("/vendor/{vendorId}/update")
    public UpdateProductResponseDto updateProduct(@PathVariable Long vendorId, @RequestBody UpdateProductRequestDto productDto) {
        return productService.updateProduct(productDto, vendorId);
    }
 
}
