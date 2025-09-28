package com.peters.cafecart.features.VendorManagement.service.Vendor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.Repository.VendorsRepository;
import com.peters.cafecart.features.VendorManagement.mappers.VendorMappers;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    VendorsRepository vendorsRepository;
    
    @Autowired
    VendorMappers vendorMappers;
    
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
    
}
