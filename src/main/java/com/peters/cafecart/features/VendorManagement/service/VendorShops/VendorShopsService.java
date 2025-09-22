package com.peters.cafecart.features.VendorManagement.service.VendorShops;

import java.util.List;

import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;

public interface VendorShopsService {

    List<VendorShopIndexCoverDto> getAllVendorShops(Long id);
    
}
