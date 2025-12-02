package com.peters.cafecart.features.ShopProductManagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;

@Service
public interface ShopProductService {

    List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(long vendorShopId);
    
    ShopProductDto findByProductAndVendorShop(long productId, long vendorShopId);
    
}
