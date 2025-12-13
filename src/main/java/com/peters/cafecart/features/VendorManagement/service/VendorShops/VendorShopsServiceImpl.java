package com.peters.cafecart.features.VendorManagement.service.VendorShops;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.VendorManagement.Repository.VendorShopsRepository;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopLocation;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSettingsDto;
import com.peters.cafecart.features.VendorManagement.mappers.VendorShopMappers;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;

@Service
public class VendorShopsServiceImpl implements VendorShopsService {

    @Autowired
    VendorShopsRepository vendorShopsRepository;

    @Autowired
    VendorShopMappers vendorShopMappers;

    @Override
    public Optional<VendorShop> getVendorShop(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        return vendorShopsRepository.findById(id);
    }

    @Override
    public List<VendorShopIndexCoverDto> getAllVendorShops(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        List<VendorShopIndexCover> projectionPage = vendorShopsRepository.findByVendorId(id);
        return vendorShopMappers.toIndexCoverList(projectionPage);
    }

    @Override
    public VendorShopLocationDto getVendorShopLocation(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShopLocation vendorShopLocation = vendorShopsRepository.findLatitudeAndLongitudeAndCityById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        VendorShopLocationDto vendorShopLocationDto = new VendorShopLocationDto(vendorShopLocation.getLatitude(),
                vendorShopLocation.getLongitude(), vendorShopLocation.getCity());
        return vendorShopLocationDto;
    }


    @Override
    public void updateIsOnline(Long id, Boolean isOnline) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        
                System.out.println(isOnline);                
        vendorShop.setIsOnline(isOnline);
        vendorShopsRepository.save(vendorShop);
    }

    @Override
    public void updateOnlinePayment(Long id, Boolean isOnlinePayment) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        vendorShop.setOnlinePaymentAvailable(isOnlinePayment);
        vendorShopsRepository.save(vendorShop);
    }

    @Override
    public void updateIsDeliveryAllowed(Long id, Boolean isDeliveryAllowed) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        vendorShop.setDeliveryAvailable(isDeliveryAllowed);
        vendorShopsRepository.save(vendorShop);
    }

    @Override
    public VendorShopSettingsDto getVendorShopSettings(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        VendorShopSettingsDto vendorShopSettingsDto = new VendorShopSettingsDto(vendorShop.getIsOnline(), vendorShop.isDeliveryAvailable());
        return vendorShopSettingsDto;
    }

}
