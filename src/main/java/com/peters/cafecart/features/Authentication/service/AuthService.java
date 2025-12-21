package com.peters.cafecart.features.Authentication.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.peters.cafecart.shared.dtos.Response.AuthResponse;
import com.peters.cafecart.shared.dtos.Request.LoginRequest;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    ResponseEntity<AuthResponse> customerLogin(LoginRequest request, HttpServletResponse response);
    ResponseEntity<AuthResponse> vendorShopLogin(LoginRequest request, HttpServletResponse response);
    ResponseEntity<AuthResponse> vendorLogin(LoginRequest request, HttpServletResponse response);
    
    ResponseEntity<HttpStatus> customerRegister(CustomerDto request);
    ResponseEntity<HttpStatus> vendorShopRegister(LoginRequest request);
    ResponseEntity<HttpStatus> vendorRegister(LoginRequest request);

    ResponseEntity<AuthResponse> refreshToken(String refreshToken);
    ResponseEntity<Boolean> isTokenValid(String token);
}
