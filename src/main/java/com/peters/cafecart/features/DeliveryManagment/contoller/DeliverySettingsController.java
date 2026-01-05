package com.peters.cafecart.features.DeliveryManagment.contoller;

import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.dto.AreaRequestDto;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/delivery-settings")
public class DeliverySettingsController {
    @Autowired DeliveryServiceImpl deliveryService;
    @Autowired VendorShopsServiceImpl shopsService;

    @GetMapping("/shop/delivery/settings")
    public ResponseEntity<DeliverySettingsDto> getShopDeliverySettings(@AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(deliveryService.getShopDeliverySettings(user.getId()));
    }

    @PostMapping("/shop/area")
    public ResponseEntity<HttpStatus> addShopDeliveryAreaSettings(@AuthenticationPrincipal CustomUserPrincipal user,
                                                                  @RequestBody AreaRequestDto dto) {
        VendorShop shop = shopsService.getVendorShop(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Shop Not Found"));
        deliveryService.addDeliveryAreaToShop(shop, dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/shop/area")
    public ResponseEntity<HttpStatus> updateShopDeliveryAreaSettings(
                @AuthenticationPrincipal CustomUserPrincipal user,
                @RequestBody AreaRequestDto dto) {
        VendorShop shop = shopsService.getVendorShop(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Shop Not Found"));
        deliveryService.updateDeliveryAreaToShop(shop, dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/shop/area")
    public ResponseEntity<HttpStatus> deleteShopDeliveryAreaSettings(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody AreaRequestDto dto) {
        VendorShop shop = shopsService.getVendorShop(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Shop Not Found"));
        deliveryService.deleteDeliveryAreaForShop(shop, dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
