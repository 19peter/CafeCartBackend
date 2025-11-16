package com.peters.cafecart.features.LocationManagement.service;

import org.springframework.stereotype.Service;

import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.LocationManagement.dto.GoogleDistanceResponseDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;

@Service
public interface LocationService {

    public GoogleDistanceResponseDto getDrivingDistance(CustomerLocationRequestDto customerLocationRequestDto);
    
    public boolean isWithinCity(CustomerLocationRequestDto customerLocationRequestDto, VendorShopLocationDto vendorShopLocationDto);
}
