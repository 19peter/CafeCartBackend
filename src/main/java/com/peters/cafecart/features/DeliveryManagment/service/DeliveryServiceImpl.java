package com.peters.cafecart.features.DeliveryManagment.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliverySettings;
import com.peters.cafecart.features.DeliveryManagment.repository.DeliveryAreasRepository;
import com.peters.cafecart.features.DeliveryManagment.dto.AreaRequestDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.peters.cafecart.features.DeliveryManagment.projections.DeliverySettingsDetails;
import com.peters.cafecart.features.DeliveryManagment.repository.DeliverySettingsRepository;


import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    @Autowired DeliverySettingsRepository deliverySettingsRepository;
    @Autowired DeliveryAreasRepository deliveryAreasRepository;



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

    @Override
    public void addDeliveryAreaToShop(VendorShop shop, AreaRequestDto dto) {
        DeliveryAreas deliveryAreas = new DeliveryAreas();
        deliveryAreas.setVendorShop(shop);
        deliveryAreas.setArea(dto.getArea());
        deliveryAreas.setPrice(dto.getPrice());
        deliveryAreas.setCity(deliveryAreas.getCity());
        deliveryAreasRepository.save(deliveryAreas);
    }

    @Override
    public void updateDeliveryAreaToShop(VendorShop shop, AreaRequestDto dto) {
        DeliveryAreas area = deliveryAreasRepository.findById(dto.getId()).orElseThrow(()-> new ResourceNotFoundException("Area not found"));
        area.setCity(dto.getCity());
        area.setArea(dto.getArea());
        area.setPrice(dto.getPrice());
        deliveryAreasRepository.save(area);
    }

    @Override
    public void deleteDeliveryAreaForShop(VendorShop shop, AreaRequestDto dto) {
        DeliveryAreas area = deliveryAreasRepository.findById(dto.getId()).orElseThrow(()-> new ResourceNotFoundException("Area not found"));
        deliveryAreasRepository.delete(area);
    }

    private double calculateDistanceCost(double distance, double baseFee, double ratePerKm) {
        // Round up to the nearest integer
        return Math.ceil((distance / 1000) * ratePerKm + baseFee);
    }

}
