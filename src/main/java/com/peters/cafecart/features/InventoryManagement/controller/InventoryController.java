package com.peters.cafecart.features.InventoryManagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;


@RestController
@RequestMapping(Constants.API_V1 + "/inventory")
public class InventoryController {
    @Autowired InventoryServiceImpl inventoryService;


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

    @PostMapping("/shop/update/{vendorShopId}/{productId}/{quantity}")
    public ResponseEntity<Boolean> updateProductInventoryForShop(
            @PathVariable Long vendorShopId,
            @PathVariable Long productId,
            @PathVariable int quantity
    ) {
       return ResponseEntity.ok(inventoryService.updateInventoryStock(vendorShopId, productId, quantity));
    }


}
