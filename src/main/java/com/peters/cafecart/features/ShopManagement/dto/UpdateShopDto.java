package com.peters.cafecart.features.ShopManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phoneNumber;
    private Long vendorId;
    private Boolean isActive;
}
