package com.peters.cafecart.features.InventoryManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.CategoryDto;


@RestController
@RequestMapping(Constants.API_V1 + "/inventory")
public class InventoryController {
    @Autowired InventoryServiceImpl inventoryService;


    @GetMapping("/vendor/{vendorShopId}/categories")
    public List<CategoryDto> getCategoriesByVendorShopId(
            @PathVariable Long vendorShopId) {
        return inventoryService.getCategoriesByVendorShopId(vendorShopId);
    }

    @GetMapping("/vendor/{vendorShopId}")
    public Page<VendorProductDto> getProductsByVendorShopIdAndCategory(
            @PathVariable Long vendorShopId,
            @RequestParam int quantity,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String category  
            ) {
        return inventoryService.getProductsByVendorShopIdAndCategory(vendorShopId, quantity, page, size, category);
    }

    @GetMapping("/vendor/{vendorShopId}/product/{productId}")
    public VendorProductDto getProductByVendorShopIdAndProductId(
            @PathVariable Long vendorShopId,
            @PathVariable Long productId) {
        return inventoryService.getProductByVendorShopIdAndProductId(vendorShopId, productId);
    }


}
