package com.peters.cafecart.features.DeliveryManagment.service;

import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;

public interface DeliveryService {
    public double calculateDeliveryCost(CustomerLocationRequestDto customerLocationRequestDto);
}
