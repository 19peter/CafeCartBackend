package com.peters.cafecart.features.VendorManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class VendorShopSettingsDto {
    boolean isOnline;
    boolean isDeliveryAllowed;
}
