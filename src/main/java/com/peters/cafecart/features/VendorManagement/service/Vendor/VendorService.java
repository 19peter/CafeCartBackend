package com.peters.cafecart.features.VendorManagement.service.Vendor;

import java.util.Optional;

import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;

import org.springframework.data.domain.Page;

public interface VendorService {
    Page<VendorIdNameDto> getAllVendors(int page, int size);
    Optional<VendorDto> getVendorById(Long id);
}
