package com.peters.cafecart.features.VendorManagement.controller;

import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.features.ShopManagement.dto.ShopDetailsDto;
import com.peters.cafecart.features.ShopManagement.dto.UpdateShopDto;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.request.CreateVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.CreatedVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.VendorInfoDto;
import com.peters.cafecart.workflows.ActivateVendorUseCase;
import com.peters.cafecart.workflows.AddShopUseCase;
import com.peters.cafecart.workflows.DeactivateVendorUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;

import java.util.List;
import java.util.Optional;
import com.peters.cafecart.Constants.Constants;

@RestController
@RequestMapping(Constants.CURRENT_API + "/vendors")
public class VendorController {
    @Autowired VendorServiceImpl vendorService;
    @Autowired VendorShopsServiceImpl shopsService;
    @Autowired AddShopUseCase addShopUseCase;
    @Autowired ActivateVendorUseCase activateVendorUseCase;
    @Autowired DeactivateVendorUseCase deactivateVendorUseCase;

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

    @GetMapping("/vendor")
    public ResponseEntity<VendorDto> getVendorInfo(@AuthenticationPrincipal CustomUserPrincipal user) {
        Optional<VendorDto> vendorDto = vendorService.getVendorById(user.getId());
        if (vendorDto.isPresent()) return ResponseEntity.ok(vendorDto.get());
        else throw new ResourceNotFoundException("Resource Not Found");
    }

    @GetMapping("/vendor/shops")
    public List<ShopDetailsDto> getVendorShopsDetails(@AuthenticationPrincipal CustomUserPrincipal user) {
        return  vendorService.getAllShopsDetails(user.getId());
    }

    @PostMapping("/vendor/shops/add")
    public ResponseEntity<Boolean> addShop(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody AddShopDto addShopDto) {
        addShopUseCase.execute(addShopDto, user.getId());
        return ResponseEntity.ok(true);
    }

    @PutMapping("/vendor/shops")
    public ResponseEntity<UpdateShopDto> updateShop(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody UpdateShopDto updateShopDto) {
        return ResponseEntity.ok(shopsService.updateShop(updateShopDto, user.getId()));
    }

    @PostMapping("/admin/create")
    public ResponseEntity<CreatedVendorDto> createVendor(@RequestBody CreateVendorDto vendorDto) {
        return ResponseEntity.ok(vendorService.createVendor(vendorDto));
    }

    @PostMapping("/admin/activate/{vendorId}")
    public ResponseEntity<HttpStatus> activateVendor(@PathVariable(name = "vendorId") Long vendorId) {
        activateVendorUseCase.execute(vendorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/deactivate/{vendorId}")
    public ResponseEntity<HttpStatus> deactivateVendor(@PathVariable(name = "vendorId") Long vendorId) {
        deactivateVendorUseCase.execute(vendorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<VendorInfoDto>> getVendorsForAdmin() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }
}
