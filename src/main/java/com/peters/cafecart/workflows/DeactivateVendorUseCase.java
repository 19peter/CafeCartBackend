package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeactivateVendorUseCase {
    @Autowired private VendorServiceImpl vendorService;


    public void execute(Long vendorId) {
        Vendor vendor = vendorService.getVendor(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setIsActive(Boolean.FALSE);

        vendor.getShops().forEach(shop -> shop.setIsActive(Boolean.FALSE));
        vendor.getAccessAccount().setIsActive(Boolean.FALSE);

        try {
            vendorService.saveVendor(vendor);
        } catch (Exception e) {
            throw new ValidationException("Failed Updating Vendor");
        }

    }
}
