package com.peters.cafecart.features.VendorManagement.service.VendorShops;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.VendorManagement.Repository.VendorShopsRepository;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.mappers.VendorShopMappers;

@Service
public class VendorShopsServiceImpl implements VendorShopsService {
    
    @Autowired
    VendorShopsRepository vendorShopsRepository;
    
    @Autowired
    VendorShopMappers vendorShopMappers;
    
    @Override
    public List<VendorShopIndexCoverDto> getAllVendorShops(Long id) {
        if(id == null) throw new ValidationException("Vendor ID cannot be null");
        List<VendorShopIndexCover> projectionPage = vendorShopsRepository.findByVendorId(id);
        return vendorShopMappers.toIndexCoverList(projectionPage);
    }


}
