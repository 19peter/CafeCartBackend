package com.peters.cafecart.features.ShopManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phoneNumber;
    private Boolean isActive;
    private Boolean isOnline;
    private String email;
    String vendorName;
}
