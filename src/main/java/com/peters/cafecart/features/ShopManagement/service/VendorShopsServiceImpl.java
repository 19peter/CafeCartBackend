package com.peters.cafecart.features.ShopManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.shared.dtos.Response.CustomerBasicResponse;
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

    @Autowired VendorShopsRepository vendorShopsRepository;
    @Autowired VendorShopMappers vendorShopMappers;
    @Autowired EntityManager entityManager;
 

    @Override
    public Optional<VendorShop> getVendorShop(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        return vendorShopsRepository.findById(id);
    }

    @Override
    public List<VendorShopIndexCoverDto> getAllVendorShops(String name) {
        if (name == null)
            throw new ValidationException("Vendor Name cannot be null");
        List<VendorShopIndexCover> projectionPage = vendorShopsRepository.findByVendorName(name);
        if (projectionPage.isEmpty()){
            throw new ResourceNotFoundException("Resource Not Found");
        }
        return vendorShopMappers.toIndexCoverList(projectionPage);

    }

    @Override
    public VendorShopLocationDto getVendorShopLocation(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShopLocation vendorShopLocation = vendorShopsRepository.findLatitudeAndLongitudeAndCityById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));

        return new VendorShopLocationDto(
                vendorShopLocation.getLatitude(),
                vendorShopLocation.getLongitude(),
                vendorShopLocation.getCity());
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
    public VendorShopSettingsDto getVendorShopSettings(Long id) {
        if (id == null)
            throw new ValidationException("Vendor ID cannot be null");
        VendorShop vendorShop = vendorShopsRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Vendor ID cannot be found"));
        return new VendorShopSettingsDto(
                vendorShop.getId(),
                vendorShop.getName(),
                vendorShop.getIsOnline(),
                null);
    }



    @Override
    public VendorShop addShop(AddShopDto addShopDto, Long vendorId) {
        if (vendorId == null)
            throw new ValidationException("Vendor ID cannot be null");
        Vendor vendor = entityManager.getReference(Vendor.class, vendorId);
        VendorShop vendorShop = toVendorShop(addShopDto, vendor);
        return vendorShopsRepository.save(vendorShop);
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

    @Override
    public void blockUser(Long vendorShopId, Long customerId) {
        VendorShop vendorShop = vendorShopsRepository.findById(vendorShopId).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
        Customer customer = entityManager.getReference(Customer.class, customerId);
        Set<Customer> blockedCustomers = vendorShop.getBlockedCustomers();
        if (!blockedCustomers.contains(customer)) {
            blockedCustomers.add(customer);
            vendorShopsRepository.save(vendorShop);
        }
    }

    @Override
    public void unblockUser(Long vendorShopId, Long customerId) {
        VendorShop vendorShop = vendorShopsRepository.findById(vendorShopId).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
        Customer customer = entityManager.getReference(Customer.class, customerId);
        Set<Customer> blockedCustomers = vendorShop.getBlockedCustomers();
        if (blockedCustomers.contains(customer)) {
            blockedCustomers.remove(customer);
            vendorShopsRepository.save(vendorShop);
        }
    }

    @Override
    public boolean isCustomerBlockedByShop(Long shopId, Long customerId) {
        return vendorShopsRepository.existsByIdAndBlockedCustomers_Id(shopId, customerId);
    }

    @Override
    public List<CustomerBasicResponse> getBlockedCustomers(Long vendorId) {
        VendorShop vendorShop = vendorShopsRepository.findById(vendorId).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
        Set<Customer> blockedCustomers = vendorShop.getBlockedCustomers();
        return setToDto(blockedCustomers);
    }

    private List<CustomerBasicResponse> setToDto(Set<Customer> customerSet) {
        List<CustomerBasicResponse> customerList = new ArrayList<>();
        customerSet.forEach(customer -> {
            CustomerBasicResponse customerBasicResponse = new CustomerBasicResponse();
            customerBasicResponse.setId(customer.getId());
            customerBasicResponse.setFirstName(customer.getFirstName());
            customerBasicResponse.setLastName(customer.getLastName());
            customerBasicResponse.setPhoneNumber(customer.getPhoneNumber());
            customerList.add(customerBasicResponse);
        });
        return  customerList;
    }

}
