package com.peters.cafecart.features.ProductsManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.ProductsManagement.dto.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/products")
public class ProductController {
    @Autowired private ProductServiceImpl productService;
    
    @GetMapping("/vendor/{vendorShopId}/categories")
    public List<CategoryDto> getCategoriesByVendorShopId(
            @PathVariable Long vendorShopId) {
        return productService.getCategoriesByVendorShopId(vendorShopId);
    }
}
