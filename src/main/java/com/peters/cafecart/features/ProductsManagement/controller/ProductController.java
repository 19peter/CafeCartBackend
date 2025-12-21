package com.peters.cafecart.features.ProductsManagement.controller;

import java.util.List;

import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.ProductsManagement.dto.request.ProductImageSaveDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/vendor")
    public List<ProductDto> getProductsForVendor(@AuthenticationPrincipal CustomUserPrincipal user){
        return  productService.getProductsForVendorShopByVendorId(user.getId());
    }

    @PostMapping("/vendor/add")
    public AddProductResponseDto addProduct(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody AddProductRequestDto productDto) {
        return addProductUseCase.execute(productDto, user.getId());
    }

    @PostMapping("/vendor/update")
    public UpdateProductResponseDto updateProduct(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody UpdateProductRequestDto productDto) {
        return productService.updateProduct(productDto, user.getId());
    }

    @PostMapping("/vendor/product-image")
    public ResponseEntity<Boolean> saveProductImage(@RequestBody ProductImageSaveDto productImageSaveDto) {
        return ResponseEntity.ok(productService.saveProductImage(productImageSaveDto.getProductId(), productImageSaveDto.getUploadUrl()));
    }
 
}
