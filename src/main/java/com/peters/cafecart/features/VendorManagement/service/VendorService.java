package com.peters.cafecart.features.VendorManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.peters.cafecart.features.ShopManagement.dto.ShopDetailsDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;

import com.peters.cafecart.features.VendorManagement.dto.request.CreateVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.CreatedVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.VendorInfoDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import org.springframework.data.domain.Page;

public interface VendorService {
    Page<VendorIdNameDto> getAllVendors(int page, int size);

    Optional<VendorDto> getVendorById(Long id);

    Optional<Vendor> getVendor(Long id);

    Set<Long> getShopIdsByVendorId(Long vendorId);

    List<ShopDetailsDto> getAllShopsDetails(Long vendorId);

    CreatedVendorDto createVendor(CreateVendorDto createVendorDto);

    List<VendorInfoDto> getAllVendors();

    boolean vendorExistsById(Long vendorId);

}
