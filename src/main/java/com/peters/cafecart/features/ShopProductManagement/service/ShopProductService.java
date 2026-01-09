package com.peters.cafecart.features.ShopProductManagement.service;

import java.util.List;
import java.util.Set;

import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductAvailabilityView;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;

@Service
public interface ShopProductService {

    boolean addAProductToAllShops(Long productId, Set<Long> vendorShopIds, boolean isAvailable);

    boolean addAllProductsToAShop(Long vendorShopId, Set<Long> productIds, boolean isAvailable);

    List<ShopProductDto> findAllForVendorShop(long vendorShopId);

    Set<Long> findAllProductIdsForVendorShop(long vendorShopId);

    List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(long vendorShopId);
    
    ShopProductDto findByProductAndVendorShop(long productId, long vendorShopId);

    boolean publishShopProduct(long productId, long vendorShopId);

    boolean unpublishShopProduct(long productId, long vendorShopId);

    ShopProductAvailabilityView getShopProductAvailability(Long productId, Long shopId);
}
