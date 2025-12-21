package com.peters.cafecart.features.ShopManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String phoneNumber;
    private String email;
    private double latitude;
    private double longitude;
    private Long vendorId;
    private boolean isOnline;
    private boolean isDeliveryAvailable;
    private boolean isOnlinePaymentAvailable;
    private String logoUrl;
    private boolean isActive;
}
