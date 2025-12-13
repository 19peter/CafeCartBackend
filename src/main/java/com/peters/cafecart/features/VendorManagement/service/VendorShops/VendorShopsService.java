package com.peters.cafecart.features.VendorManagement.service.VendorShops;

import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;

public interface VendorShopsService {

    Optional<VendorShop> getVendorShop(Long id);

    List<VendorShopIndexCoverDto> getAllVendorShops(Long id);

    VendorShopLocationDto getVendorShopLocation(Long id);

    void updateIsOnline(Long id, Boolean isOnline);

    void updateOnlinePayment(Long id, Boolean isOnlinePayment);

    void updateIsDeliveryAllowed(Long id, Boolean isDeliveryAllowed);
    
}
