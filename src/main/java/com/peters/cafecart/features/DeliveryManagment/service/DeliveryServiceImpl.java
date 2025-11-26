package com.peters.cafecart.features.DeliveryManagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.projections.DeliverySettingsDetails;
import com.peters.cafecart.features.DeliveryManagment.repository.DeliverySettingsRepository;
import com.peters.cafecart.features.LocationManagement.dto.GoogleDistanceResponseDto;
import com.peters.cafecart.features.LocationManagement.service.LocationServiceImpl;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    @Autowired
    LocationServiceImpl locationService;
    @Autowired
    DeliverySettingsRepository deliverySettingsRepository;

    @Override
    @Cacheable(
        value = "deliveryFee",
        key = "#customerLocationRequestDto.shopId + '_' + #customerLocationRequestDto.latitude + '_' + #customerLocationRequestDto.longitude")
    public double calculateDeliveryCost(CustomerLocationRequestDto customerLocationRequestDto) {
        DeliverySettingsDetails deliverySettingsDetails = deliverySettingsRepository
                .findByVendorShopId(customerLocationRequestDto.getShopId())
                .orElseThrow(() -> new UnauthorizedAccessException("Delivery Service Not Available To This Area"));

        GoogleDistanceResponseDto distanceResponseDto = locationService.getDrivingDistance(customerLocationRequestDto);
        double distance = distanceResponseDto.getRows().get(0).getElements().get(0).getDistance().getValue();
        return calculateDistanceCost(distance, deliverySettingsDetails.getBaseFee(),
                deliverySettingsDetails.getRatePerKm());
    }

    private double calculateDistanceCost(double distance, double baseFee, double ratePerKm) {
        // Round up to the nearest integer
        return Math.ceil((distance / 1000) * ratePerKm + baseFee);
    }

}
