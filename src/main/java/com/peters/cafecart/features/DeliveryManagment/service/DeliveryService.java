package com.peters.cafecart.features.DeliveryManagment.service;

import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import com.peters.cafecart.features.DeliveryManagment.dto.AreaRequestDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

public interface DeliveryService {

    DeliverySettingsDto getShopDeliverySettings(VendorShop shop);
    DeliverySettingsDto getShopDeliverySettings(Long shopId);

    void updateShopDeliverySettings(VendorShop shop);
    void updateShopDeliverySettings(Long shopId);

    void createDefaultDeliverySettingsForShop(VendorShop shop);
    void updateIsDeliveryAvailable(Long shopId, Boolean isAvailable);

    DeliveryAreas findDeliveryAreaById(Long id);

    void addDeliveryAreaToShop(VendorShop shop, AreaRequestDto dto);
    void updateDeliveryAreaToShop(VendorShop shop, AreaRequestDto dto);
    void deleteDeliveryAreaForShop(VendorShop shop, AreaRequestDto dto);
}
