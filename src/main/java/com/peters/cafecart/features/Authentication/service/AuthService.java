package com.peters.cafecart.features.Authentication.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.peters.cafecart.shared.dtos.AuthResponse;
import com.peters.cafecart.shared.dtos.LoginRequest;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;

public interface AuthService {
    ResponseEntity<AuthResponse> customerLogin(LoginRequest request);
    ResponseEntity<AuthResponse> vendorShopLogin(LoginRequest request);
    ResponseEntity<AuthResponse> vendorLogin(LoginRequest request);
    
    ResponseEntity<HttpStatus> customerRegister(CustomerDto request);
    ResponseEntity<HttpStatus> vendorShopRegister(LoginRequest request);
    ResponseEntity<HttpStatus> vendorRegister(LoginRequest request);
}
