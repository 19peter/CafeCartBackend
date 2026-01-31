package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivateVendorUseCase {
    @Autowired private VendorServiceImpl vendorService;

    public void execute(Long vendorId) {
        Vendor vendor = vendorService.getVendor(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setIsActive(Boolean.TRUE);

        vendor.getShops().forEach(shop -> shop.setIsActive(Boolean.TRUE));
        vendor.getAccessAccount().setIsActive(Boolean.TRUE);


        try {
            vendorService.saveVendor(vendor);
        } catch (Exception e) {
            throw new ValidationException("Failed Updating Vendor");
        }
    }
}
