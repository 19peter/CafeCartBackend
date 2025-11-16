package com.peters.cafecart.features.Authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.Authentication.service.AuthServiceImpl;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.shared.dtos.LoginRequest;

@RestController
@RequestMapping(Constants.CURRENT_API + "/auth")
public class AuthController {
    
    @Autowired AuthServiceImpl authService;

    @PostMapping("/login/customer")
    public ResponseEntity<?> customerLogin(@RequestBody LoginRequest request) {
        return authService.customerLogin(request);
    }

    @PostMapping("/login/vendor-shop")
    public ResponseEntity<?> vendorShopLogin(@RequestBody LoginRequest request) {
        return authService.vendorShopLogin(request);
    }

    @PostMapping("/login/vendor")
    public ResponseEntity<?> vendorLogin(@RequestBody LoginRequest request) {
        return authService.vendorLogin(request);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> customerRegister(@RequestBody CustomerDto request) {
        return authService.customerRegister(request);
    }

    @PostMapping("/register/vendor-shop")
    public ResponseEntity<?> vendorShopRegister(@RequestBody LoginRequest request) {
        return authService.vendorShopRegister(request);
    }

    @PostMapping("/register/vendor")
    public ResponseEntity<?> vendorRegister(@RequestBody LoginRequest request) {
        return authService.vendorRegister(request);
    }

}
