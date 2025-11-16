package com.peters.cafecart.features.InventoryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;

public interface InventoryService {
    Page<VendorProductDto> getProductsByVendorShopId(
        Long vendorShopId,
        int quantity,
        int page,
        int size);

    VendorProductDto getProductByVendorShopIdAndProductId(
        Long vendorShopId,
        Long productId);
   
    void reduceInventoryStockInBulk(Long vendorShopId, List<CartItemDto> orderItems);


    Optional<ShopProductSummary> getShopProductSummaryByVendorShopIdAndProductId(
        Long vendorShopId,
        Long productId);
}
