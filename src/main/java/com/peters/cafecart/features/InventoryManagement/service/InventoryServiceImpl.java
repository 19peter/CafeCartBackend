package com.peters.cafecart.features.InventoryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.ProductsManagement.dto.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.mappers.InventoryMappers;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired InventoryRepository inventoryRepository;
    @Autowired InventoryMappers inventoryMappers;
    @Autowired ProductServiceImpl productService;

    @Override
    public Page<VendorProductDto> getProductsByVendorShopIdAndCategory(
            Long vendorShopId,
            int quantity,
            int page,
            int size,
            String category) {
        if(vendorShopId == null) throw new ValidationException("Vendor Shop ID cannot be null");
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorProduct> vendorProductPage = inventoryRepository.findByVendorShopIdAndQuantityGreaterThanAndCategory(
            vendorShopId,
            quantity,
            category,
            pageable);
        return inventoryMappers.toDtoPage(vendorProductPage);
    }

    @Override
    public VendorProductDto getProductByVendorShopIdAndProductId(
            Long vendorShopId,
            Long productId) {
        if(vendorShopId == null || productId == null) throw new ValidationException("Vendor Shop ID and Product ID cannot be null");
        Optional<VendorProduct> vendorProduct = inventoryRepository.findByVendorShopIdAndProductId(vendorShopId, productId);
        if (vendorProduct.isEmpty()) throw new ResourceNotFoundException("Product not found");
        return inventoryMappers.toDto(vendorProduct.get());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void reduceInventoryStockInBulk(Long vendorShopId, List<CartItemDto> orderItems) {
        try {
            if(vendorShopId == null || orderItems.isEmpty()) throw new ValidationException("Vendor Shop ID and Order Items cannot be null");
            for (CartItemDto orderItem : orderItems) {
                Long productId = orderItem.getProductId();
                int quantity = orderItem.getQuantity();
                if(productId == null || quantity <= 0) throw new ValidationException("Product ID and Quantity cannot be null or less than or equal to zero");
                inventoryRepository.reduceInventoryStock(vendorShopId, productId, quantity);
            }
        } catch (Exception e) {
            throw new ValidationException("Failed to reduce inventory stock " + e.getMessage());
        }
    }

    @Override
    public Optional<ShopProductSummary> getShopProductSummaryByVendorShopIdAndProductId(
            Long vendorShopId,
            Long productId) {
        if(vendorShopId == null || productId == null) throw new ValidationException("Vendor Shop ID and Product ID cannot be null");
        Optional<ShopProductSummary> shopProductSummary = inventoryRepository.findShopProductSummaryByVendorShopIdAndProductId(vendorShopId, productId);
        return shopProductSummary;
    }

    @Override
    public List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId) {
        if(vendorShopId == null) throw new ValidationException("Vendor Shop ID cannot be null");
        return productService.getCategoriesByVendorShopId(vendorShopId);
    }
}
