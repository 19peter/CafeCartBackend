package com.peters.cafecart.features.InventoryManagement.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.mappers.InventoryMappers;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    InventoryMappers inventoryMappers;

    @Override
    public Page<VendorProductDto> getProductsByVendorId(
            Long vendorId,
            int quantity,
            int page,
            int size) {
        if(vendorId == null) throw new ValidationException("Vendor ID cannot be null");
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorProduct> vendorProductPage = inventoryRepository.findByVendorShopIdAndQuantityGreaterThan(vendorId,
                quantity, pageable);
        return inventoryMappers.toDtoPage(vendorProductPage);
    }

    @Override
    public VendorProductDto getProductByVendorIdAndProductId(
            Long vendorId,
            Long productId) {
        if(vendorId == null || productId == null) throw new ValidationException("Vendor ID and Product ID cannot be null");
        Optional<VendorProduct> vendorProduct = inventoryRepository.findByVendorShopIdAndProductId(vendorId, productId);
        if (vendorProduct.isEmpty()) throw new ResourceNotFoundException("Product not found");
        return inventoryMappers.toDto(vendorProduct.get());
    }

}
