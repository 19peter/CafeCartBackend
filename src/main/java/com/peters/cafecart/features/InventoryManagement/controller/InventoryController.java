package com.peters.cafecart.features.InventoryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired InventoryServiceImpl inventoryService;

    @GetMapping("/vendor/{vendorId}")
    public Page<VendorProductDto> getProductsByVendorId(
            @PathVariable Long vendorId,
            @RequestParam int quantity,
            Pageable pageable) {
        return inventoryService.getProductsByVendorId(vendorId, quantity, pageable);
    }
}
