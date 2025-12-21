package com.peters.cafecart.features.VendorManagement.service;

import java.util.Optional;
import java.util.Set;

import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;

import org.springframework.data.domain.Page;

public interface VendorService {
    Page<VendorIdNameDto> getAllVendors(int page, int size);

    Optional<VendorDto> getVendorById(Long id);

    Set<Long> getShopIdsByVendorId(Long vendorId);

}
