package com.peters.cafecart.features.ShopManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddShopDto {
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
    private String email;
    private String password;
    private double latitude;
    private double longitude;
    private Long vendorId;
    private boolean isOnline;
    private boolean isDeliveryAvailable;
    private boolean isOnlinePaymentAvailable;
    private String logoUrl;
    private boolean isActive;
}
