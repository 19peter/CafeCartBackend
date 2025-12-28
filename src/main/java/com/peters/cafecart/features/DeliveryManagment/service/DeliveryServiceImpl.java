package com.peters.cafecart.features.DeliveryManagment.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliverySettings;
import com.peters.cafecart.features.DeliveryManagment.repository.DeliveryAreasRepository;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.projections.DeliverySettingsDetails;
import com.peters.cafecart.features.DeliveryManagment.repository.DeliverySettingsRepository;
import com.peters.cafecart.features.LocationManagement.dto.GoogleDistanceResponseDto;
import com.peters.cafecart.features.LocationManagement.service.LocationServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    @Autowired LocationServiceImpl locationService;
    @Autowired DeliverySettingsRepository deliverySettingsRepository;
    @Autowired DeliveryAreasRepository deliveryAreasRepository;

    @Override
    @Cacheable(
        value = "deliveryFee",
        key = "#customerLocationRequestDto.shopId + '_' + #customerLocationRequestDto.latitude + '_' + #customerLocationRequestDto.longitude")
    public double calculateDeliveryCost(CustomerLocationRequestDto customerLocationRequestDto) {
        DeliverySettingsDetails deliverySettingsDetails = deliverySettingsRepository
                .findByVendorShopId(customerLocationRequestDto.getShopId())
                .orElseThrow(() -> new UnauthorizedAccessException("Delivery Service Not Available To This Area"));

        GoogleDistanceResponseDto distanceResponseDto = locationService.getDrivingDistance(customerLocationRequestDto);
        double distance = distanceResponseDto.getRows().getFirst().getElements().getFirst().getDistance().getValue();
        return calculateDistanceCost(distance, deliverySettingsDetails.getBaseFee(),
                deliverySettingsDetails.getRatePerKm());
    }

    @Override
    public DeliverySettingsDto getShopDeliverySettings(VendorShop shop) {
        DeliverySettingsDetails deliverySettingsDetails =
                deliverySettingsRepository.findByVendorShopId(shop.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Delivery Not Available: Delivery Details Not Found"));

        DeliverySettingsDto deliverySettingsDto = new DeliverySettingsDto();
        deliverySettingsDto.setDeliverySettingsEnum(deliverySettingsDetails.getDeliveryApproach());

        if (deliverySettingsDetails.getDeliveryApproach().equals(DeliverySettingsEnum.AREA)) {
            List<DeliveryAreas> deliveryAreasList = deliveryAreasRepository.findAllByVendorShopId(shop.getId());
            List<DeliveryAreasDto> deliveryAreasDtoList = new ArrayList<>();
            deliveryAreasList.forEach(a -> {
                DeliveryAreasDto deliveryAreasDto = new DeliveryAreasDto();
                deliveryAreasDto.setId(a.getId());
                deliveryAreasDto.setPrice(a.getPrice().doubleValue());
                deliveryAreasDto.setArea(a.getArea());
                deliveryAreasDto.setCity(a.getCity());
                deliveryAreasDtoList.add(deliveryAreasDto);
            });
            deliverySettingsDto.setDeliveryAreasDtoList(deliveryAreasDtoList);
        } else {
            deliverySettingsDto.setBaseFee(deliverySettingsDetails.getBaseFee());
            deliverySettingsDto.setRatePerKM(deliverySettingsDetails.getRatePerKm());
        }
        return deliverySettingsDto;
    }

    @Override
    public void updateShopDeliverySettings(VendorShop shop) {

    }

    @Override
    public DeliverySettingsDto getShopDeliverySettings(Long shopId) {
        DeliverySettings deliverySettings =
                deliverySettingsRepository.findDeliverySettingsByVendorShopId(shopId)
                        .orElseThrow(() -> new ResourceNotFoundException("Delivery Not Available: Delivery Details Not Found"));


        DeliverySettingsDto deliverySettingsDto = new DeliverySettingsDto();
        deliverySettingsDto.setDeliverySettingsEnum(deliverySettings.getDeliveryApproach());
        deliverySettingsDto.setDeliveryAvailable(deliverySettings.getIsDeliveryAvailable());

        if (deliverySettings.getDeliveryApproach().equals(DeliverySettingsEnum.AREA)) {
            List<DeliveryAreas> deliveryAreasList = deliveryAreasRepository.findAllByVendorShopId(shopId);
            List<DeliveryAreasDto> deliveryAreasDtoList = new ArrayList<>();
            deliveryAreasList.forEach(a -> {
                DeliveryAreasDto deliveryAreasDto = new DeliveryAreasDto();
                deliveryAreasDto.setId(a.getId());
                deliveryAreasDto.setPrice(a.getPrice().doubleValue());
                deliveryAreasDto.setArea(a.getArea());
                deliveryAreasDto.setCity(a.getCity());
                deliveryAreasDtoList.add(deliveryAreasDto);
            });
            deliverySettingsDto.setDeliveryAreasDtoList(deliveryAreasDtoList);
        } else {
            deliverySettingsDto.setBaseFee(deliverySettings.getBaseFee());
            deliverySettingsDto.setRatePerKM(deliverySettings.getRatePerKm());
        }
        return deliverySettingsDto;
    }

    @Override
    public void updateShopDeliverySettings(Long shopId) {

    }

    @Override
    public void createDefaultDeliverySettingsForShop(VendorShop shop) {
        DeliverySettings settings = new DeliverySettings();
        settings.setIsDeliveryAvailable(false);
        settings.setDeliveryApproach(DeliverySettingsEnum.AREA);
        settings.setVendorShop(shop);
        deliverySettingsRepository.save(settings);
    }

    @Override
    public void updateIsDeliveryAvailable(Long shopId, Boolean isAvailable) {
        DeliverySettings settings = deliverySettingsRepository.findDeliverySettingsByVendorShopId(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
        if (settings.getDeliveryApproach() == null) {
            settings.setDeliveryApproach(DeliverySettingsEnum.AREA);
        }
        settings.setIsDeliveryAvailable(isAvailable);
        deliverySettingsRepository.save(settings);
    }

    @Override
    public DeliveryAreas findDeliveryAreaById(Long id) {
        return deliveryAreasRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Resource not found"));
    }

    private double calculateDistanceCost(double distance, double baseFee, double ratePerKm) {
        // Round up to the nearest integer
        return Math.ceil((distance / 1000) * ratePerKm + baseFee);
    }

}
