package com.peters.cafecart.features.VendorManagement.dto;

import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class VendorShopSettingsDto {
    Long shopId;
    String shopName;
    boolean isOnline;
    DeliverySettingsDto deliverySettingsDto;
}
