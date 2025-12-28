package com.peters.cafecart.features.ShopManagement.controller;

import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.shared.dtos.Response.CustomerBasicResponse;
import com.peters.cafecart.workflows.AddShopUseCase;
import org.springframework.web.bind.annotation.*;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ProductsManagement.dto.response.VendorProductToShopResponseDto;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.BoolDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSettingsDto;
import com.peters.cafecart.features.ShopManagement.dto.UpdateShopDto;
import com.peters.cafecart.features.VendorManagement.service.VendorService;
import com.peters.cafecart.workflows.CreateVendorShopProductsUseCase;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping(Constants.API_V1 + "/vendor-shops")
public class VendorShopsController {
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired VendorService vendorService;
    @Autowired CreateVendorShopProductsUseCase createVendorShopProductsUseCase;
    @Autowired AddShopUseCase addShopUseCase;
    @Autowired CustomerServiceImpl customerService;
    @Autowired DeliveryServiceImpl deliveryService;

    @GetMapping("/{vendorName}")
    public List<VendorShopIndexCoverDto> getAllVendorShops(@PathVariable String vendorName) {
        return vendorShopsService.getAllVendorShops(vendorName);
    }

    @GetMapping("/shop/vendor/products")
    public List<VendorProductToShopResponseDto> getVendorProductsForVendorShop(@AuthenticationPrincipal CustomUserPrincipal user) {
        return createVendorShopProductsUseCase.execute(user.getId());
    }

     @PostMapping("/shop")
     public ResponseEntity<Boolean> addShop(@RequestBody AddShopDto addShopDto) {
         Long vendorId = addShopDto.getVendorId();
         addShopUseCase.execute(addShopDto, vendorId);
         return ResponseEntity.ok(true);
     }

    @PutMapping("/shop")
    public ResponseEntity<UpdateShopDto> updateShop(@RequestBody UpdateShopDto updateShopDto) {
        Long vendorId = updateShopDto.getVendorId();
        validateVendor(vendorId);
        return ResponseEntity.ok(vendorShopsService.updateShop(updateShopDto, vendorId));
    }

    @GetMapping("/shop/settings")
    public VendorShopSettingsDto getVendorShopSettings(@AuthenticationPrincipal CustomUserPrincipal user) {
        DeliverySettingsDto deliverySettingsDto = deliveryService.getShopDeliverySettings(user.getId());
        VendorShopSettingsDto shopSettingsDto = vendorShopsService.getVendorShopSettings(user.getId());
        shopSettingsDto.setDeliverySettingsDto(deliverySettingsDto);
        return shopSettingsDto;
    }

    @GetMapping("/shop/delivery/settings")
    public void getShopDeliverySettings() {

    }

    @PostMapping("/shop/delivery/settings")
    public void updateShopDeliverySettings() {

    }

    @PutMapping("/shop/set-online") 
    public ResponseEntity<HttpStatus> updateIsOnline(@AuthenticationPrincipal CustomUserPrincipal user,
                                                     @RequestBody BoolDto isOnline) {
        vendorShopsService.updateIsOnline(user.getId(), isOnline.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/shop/set-online-payment") 
    public ResponseEntity<HttpStatus> updateOnlinePayment(@AuthenticationPrincipal CustomUserPrincipal user,
                                                          @RequestBody BoolDto isOnlinePayment) {
        vendorShopsService.updateOnlinePayment(user.getId(), isOnlinePayment.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/shop/set-delivery") 
    public ResponseEntity<HttpStatus> updateIsDeliveryAllowed(@AuthenticationPrincipal CustomUserPrincipal user,
                                                              @RequestBody BoolDto isDeliveryAllowed) {
        deliveryService.updateIsDeliveryAvailable(user.getId(), isDeliveryAllowed.isValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/shop/blocked-users")
    public ResponseEntity<List<CustomerBasicResponse>> getBlockedUserForVendor(
            @AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(vendorShopsService.getBlockedCustomers(user.getId()));
    }

    @PostMapping("/shop/block/{id}")
    public ResponseEntity<HttpStatus> blockUser(@AuthenticationPrincipal CustomUserPrincipal user,
                                                @PathVariable("id") Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        vendorShopsService.blockUser(user.getId(), customer.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/shop/unblock/{id}")
    public ResponseEntity<HttpStatus> unblockUser(@AuthenticationPrincipal CustomUserPrincipal user,
                                                  @PathVariable("id") Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        vendorShopsService.unblockUser(user.getId(), customer.getId());
        return ResponseEntity.ok(HttpStatus.OK);

    }

    private void validateVendor(Long vendorId) {
        Optional<VendorDto> vendorCheck = vendorService.getVendorById(vendorId);
        if (vendorCheck.isEmpty()) throw new ValidationException("Vendor not found");
        if (!vendorCheck.get().getIsActive()) throw new ValidationException("Vendor is not active");
    }

}
