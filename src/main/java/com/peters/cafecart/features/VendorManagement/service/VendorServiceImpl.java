package com.peters.cafecart.features.VendorManagement.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.Repository.VendorsRepository;
import com.peters.cafecart.features.VendorManagement.mappers.VendorMappers;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired VendorsRepository vendorsRepository;
    @Autowired VendorMappers vendorMappers;
    @Autowired ProductServiceImpl productService;
    @Autowired VendorShopsServiceImpl shopService;
    
    @Override
    public Page<VendorIdNameDto> getAllVendors(int page, int size) {
        if(page < 0 || size < 0) throw new ValidationException("Page and size must be greater than 0");
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorIdName> vendorsProjection = vendorsRepository.findByIsActiveTrue(pageable);
        return vendorMappers.toDtoPageIdName(vendorsProjection);
    }

    @Override
    public Optional<VendorDto> getVendorById(Long id) {
        if(id == null) throw new ValidationException("Vendor ID cannot be null");
        return vendorsRepository.findById(id).map(vendorMappers::toDto);
    }

    @Override
    public Set<Long> getShopIdsByVendorId(Long vendorId) {
        if(vendorId == null) throw new ValidationException("Vendor ID cannot be null");
        Optional<Vendor> vendor = vendorsRepository.findById(vendorId);
        if(vendor.isEmpty()) throw new ResourceNotFoundException("Vendor not found");
        return vendor.get().getShops().stream().map(VendorShop::getId).collect(Collectors.toSet());
    }
    
}
