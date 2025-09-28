package com.peters.cafecart.features.InventoryManagement.service;

import org.springframework.data.domain.Page;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;

public interface InventoryService {
    Page<VendorProductDto> getProductsByVendorId(
        Long vendorId,
        int quantity,
        int page,
        int size);

    VendorProductDto getProductByVendorIdAndProductId(
        Long vendorId,
        Long productId);

    
}
