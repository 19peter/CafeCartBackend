package com.peters.cafecart.features.Authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peters.cafecart.config.CustomUserDetailsService;
import com.peters.cafecart.config.JwtService;
import com.peters.cafecart.shared.dtos.AuthResponse;
import com.peters.cafecart.shared.dtos.LoginRequest;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.service.CustomerService;
import com.peters.cafecart.config.CustomUserPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired CustomUserDetailsService userDetailsService;
    @Autowired CustomerService customerService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;

    @Override
    public ResponseEntity<AuthResponse> customerLogin(LoginRequest request) {
        System.out.println(request.email());
        CustomUserPrincipal user = userDetailsService.loadCustomerByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateTokens(user);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorShopLogin(LoginRequest request) {
        CustomUserPrincipal user = userDetailsService.loadVendorShopByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateTokens(user);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorLogin(LoginRequest request) {
        CustomUserPrincipal user = userDetailsService.loadVendorAccessAccountByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateTokens(user);
    }

    @Override
    public ResponseEntity<HttpStatus> customerRegister(CustomerDto request) {
        customerService.createCustomer(request);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<HttpStatus> vendorShopRegister(LoginRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> vendorRegister(LoginRequest request) {
        return null;
    }


    private ResponseEntity<AuthResponse> generateTokens(CustomUserPrincipal user) {
        String access = jwtService.generateToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(access, refresh));
    }
}


