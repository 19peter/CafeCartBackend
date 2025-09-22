package com.peters.cafecart.features.InventoryManagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;

public interface InventoryService {
    Page<VendorProductDto> getProductsByVendorId(
        Long vendorId,
        int quantity,
        Pageable pageable);
}
