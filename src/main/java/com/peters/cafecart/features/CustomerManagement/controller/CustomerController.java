package com.peters.cafecart.features.CustomerManagement.controller;

import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.CustomerManagement.dto.AddressDto;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerBasicInfoDto;
import com.peters.cafecart.features.CustomerManagement.dto.PhoneDto;
import com.peters.cafecart.features.CustomerManagement.service.CustomerService;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.peters.cafecart.Constants.Constants;



@RestController
@RequestMapping(Constants.API_V1 + "/customer")
public class CustomerController  {

    @Autowired CustomerServiceImpl customerService;

    @GetMapping("/info")
    public CustomerBasicInfoDto getCustomerBasicInfo(@AuthenticationPrincipal CustomUserPrincipal user) {
        return customerService.getCustomerBasicInfo(user.getId());
    }

    @PostMapping("/address")
    public ResponseEntity<HttpStatus> updateCustomerAddress(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody AddressDto addressDto) {
        customerService.updateAddress(user.getId(), addressDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping("/phone")
    public ResponseEntity<HttpStatus> updateCustomerPhone(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody PhoneDto phoneDto) {
        customerService.updatePhone(user.getId(), phoneDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
