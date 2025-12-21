package com.peters.cafecart.features.VendorManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import java.util.Optional;
import com.peters.cafecart.Constants.Constants;

@RestController
@RequestMapping(Constants.CURRENT_API + "/vendors")
public class VendorController {
    
    @Autowired VendorServiceImpl vendorService;

    @GetMapping
    public Page<VendorIdNameDto> getAllVendors(
            @RequestParam int page,
            @RequestParam int size) {
        return vendorService.getAllVendors(page, size);
    }

    @GetMapping("/{id}")
    public Optional<VendorDto> getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

}
