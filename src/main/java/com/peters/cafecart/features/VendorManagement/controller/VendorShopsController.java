package com.peters.cafecart.features.VendorManagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.VendorManagement.dto.BoolDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSettingsDto;
import com.peters.cafecart.features.VendorManagement.service.VendorShops.VendorShopsServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping(Constants.API_V1 + "/vendor-shops")
public class VendorShopsController {
    @Autowired VendorShopsServiceImpl vendorShopsService;

    @GetMapping("/{id}") 
    public List<VendorShopIndexCoverDto> getAllVendorShops(@PathVariable Long id) {
        return vendorShopsService.getAllVendorShops(id);
    }

    @GetMapping("/shop/settings")
    public VendorShopSettingsDto getVendorShopSettings(@AuthenticationPrincipal CustomUserPrincipal user) {
        return vendorShopsService.getVendorShopSettings(user.getId());
    }

    @PutMapping("/shop/set-online") 
    public ResponseEntity<HttpStatus> updateIsOnline(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody BoolDto isOnline) {
        vendorShopsService.updateIsOnline(user.getId(), isOnline.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/shop/set-online-payment") 
    public ResponseEntity<HttpStatus> updateOnlinePayment(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody BoolDto isOnlinePayment) {
        vendorShopsService.updateOnlinePayment(user.getId(), isOnlinePayment.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/shop/set-delivery") 
    public ResponseEntity<HttpStatus> updateIsDeliveryAllowed(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody BoolDto isDeliveryAllowed) {
        vendorShopsService.updateIsDeliveryAllowed(user.getId(), isDeliveryAllowed.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
