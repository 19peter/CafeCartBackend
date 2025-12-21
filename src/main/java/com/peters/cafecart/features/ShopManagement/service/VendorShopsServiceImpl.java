package com.peters.cafecart.features.ShopManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.features.ShopManagement.dto.UpdateShopDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.mappers.VendorShopMappers;
import com.peters.cafecart.features.ShopManagement.repository.VendorShopsRepository;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopLocation;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopLocationDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSettingsDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

import jakarta.persistence.EntityManager;

@Service
public class VendorShopsServiceImpl implements VendorShopsService {

    @Autowired
    VendorShopsRepository vendorShopsRepository;
    @Autowired
    VendorShopMappers vendorShopMappers;
    @Autowired
    EntityManager entityManager;
 

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
        VendorShopSettingsDto vendorShopSettingsDto = new VendorShopSettingsDto(vendorShop.getIsOnline(),
                vendorShop.isDeliveryAvailable());
        return vendorShopSettingsDto;
    }



    @Override
    public VendorShop addShop(AddShopDto addShopDto, Long vendorId) {
        if (vendorId == null)
            throw new ValidationException("Vendor ID cannot be null");
        Vendor vendor = entityManager.getReference(Vendor.class, vendorId);
        VendorShop vendorShop = toVendorShop(addShopDto, vendor);
        if (vendorShop == null)
            throw new ValidationException("Vendor Shop cannot be null");
        VendorShop savedVendorShop = vendorShopsRepository.save(vendorShop);
        return savedVendorShop;
    }

    @Override
    public UpdateShopDto updateShop(UpdateShopDto updateShopDto, Long vendorId) {
        if (vendorId == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(vendorId)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        vendorShop.setName(updateShopDto.getName());
        vendorShop.setAddress(updateShopDto.getAddress());
        vendorShop.setLatitude(updateShopDto.getLatitude());
        vendorShop.setLongitude(updateShopDto.getLongitude());
        vendorShop.setCity(updateShopDto.getCity());
        vendorShop.setPhoneNumber(updateShopDto.getPhoneNumber());
        vendorShop.setEmail(updateShopDto.getEmail());
        vendorShop.setIsOnline(updateShopDto.isOnline());
        vendorShop.setLogoUrl(updateShopDto.getLogoUrl());
        vendorShop.setIsActive(updateShopDto.isActive());
        vendorShop.setCreatedAt(LocalDateTime.now());
        vendorShop.setUpdatedAt(LocalDateTime.now());
        vendorShopsRepository.save(vendorShop);
        return updateShopDto;
    }

    private VendorShop toVendorShop(AddShopDto addShopDto, Vendor vendor) {
        VendorShop vendorShop = new VendorShop();
        vendorShop.setVendor(vendor);
        vendorShop.setName(addShopDto.getName());
        vendorShop.setAddress(addShopDto.getAddress());
        vendorShop.setLatitude(addShopDto.getLatitude());
        vendorShop.setLongitude(addShopDto.getLongitude());
        vendorShop.setCity(addShopDto.getCity());
        vendorShop.setPhoneNumber(addShopDto.getPhoneNumber());
        vendorShop.setEmail(addShopDto.getEmail());
        vendorShop.setPassword(addShopDto.getPassword());
        vendorShop.setIsOnline(addShopDto.isOnline());
        vendorShop.setOnlinePaymentAvailable(addShopDto.isOnlinePaymentAvailable());
        vendorShop.setLogoUrl(addShopDto.getLogoUrl());
        vendorShop.setIsActive(addShopDto.isActive());
        vendorShop.setCreatedAt(LocalDateTime.now());
        vendorShop.setUpdatedAt(LocalDateTime.now());
        return vendorShop;
    }

}
