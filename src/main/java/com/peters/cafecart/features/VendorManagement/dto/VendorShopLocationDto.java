package com.peters.cafecart.features.VendorManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorShopLocationDto {
    private double latitude;
    private double longitude;
    private String city;

    public VendorShopLocationDto(double latitude, double longitude, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }
}
