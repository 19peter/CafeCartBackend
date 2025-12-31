package com.peters.cafecart.features.ShopManagement.service;

import java.util.List;
import java.util.Optional;

import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.features.ShopManagement.dto.ShopDto;
import com.peters.cafecart.features.ShopManagement.dto.UpdateShopDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSettingsDto;
import com.peters.cafecart.shared.dtos.Response.CustomerBasicResponse;

public interface VendorShopsService {

    Optional<VendorShop> getVendorShop(Long id);

//    List<VendorShopIndexCoverDto> getAllVendorShops(Long id);

    List<VendorShopIndexCoverDto> getAllVendorShops(String name);

    VendorShopLocationDto getVendorShopLocation(Long id);

    void updateIsOnline(Long id, Boolean isOnline);

    void updateOnlinePayment(Long id, Boolean isOnlinePayment);


    VendorShopSettingsDto getVendorShopSettings(Long id);

    VendorShop addShop(AddShopDto addShopDto, Long vendorId);

    UpdateShopDto updateShop(UpdateShopDto updateShopDto, Long vendorId);

    void blockUser(Long vendorShopId, Long customerId);

    void unblockUser(Long vendorShopId, Long customerId);

    boolean isCustomerBlockedByShop(Long shopId, Long customerId);

    List<CustomerBasicResponse> getBlockedCustomers(Long vendorShopId);

    List<ShopDto> getAllShopsForAdmin();
}
