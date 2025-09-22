package com.peters.cafecart.features.InventoryManagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.mappers.InventoryMappers;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired InventoryRepository inventoryRepository;
    @Autowired InventoryMappers inventoryMappers;

    @Override
    public Page<VendorProductDto> getProductsByVendorId(
        Long vendorId,
        int quantity,
        Pageable pageable) {
        Page<VendorProduct> page = inventoryRepository.findByVendorShopIdAndQuantityGreaterThan(vendorId, quantity, pageable);
        return inventoryMappers.toDtoPage(page);
    }
}
