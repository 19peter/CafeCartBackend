package com.peters.cafecart.features.VendorManagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.service.VendorShops.VendorShopsServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(Constants.API_V1 + "/vendor-shops")
public class VendorShopsController {
    @Autowired
    VendorShopsServiceImpl vendorShopsService;
    
    @GetMapping("/{id}")
    public List<VendorShopIndexCoverDto> getAllVendorShops(@PathVariable Long id) {
        return vendorShopsService.getAllVendorShops(id);
    }
    
   
    
}
