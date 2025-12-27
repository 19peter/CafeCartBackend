package com.peters.cafecart.features.VerifiedCustomerManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import com.peters.cafecart.features.VerifiedCustomerManagement.service.VerifiedCustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_V1 + "/verify")
public class VerifiedCustomerController {

    @Autowired VerifiedCustomerServiceImpl verifiedCustomerService;
    @Autowired CustomerServiceImpl customerService;
    @Autowired VendorShopsServiceImpl shopsService;

    @PostMapping("/shop/{customerId}")
    public ResponseEntity<Boolean> verifyCustomer(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable("customerId") Long customerId) {

        Customer customer = customerService.getCustomerById(customerId);
        VendorShop shop = shopsService.getVendorShop(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));
        Vendor vendor = shop.getVendor();

        return ResponseEntity.ok(verifiedCustomerService.verifyCustomer(vendor, shop, customer));
    }
}
