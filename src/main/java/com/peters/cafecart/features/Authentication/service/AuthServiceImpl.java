package com.peters.cafecart.features.Authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peters.cafecart.config.CustomUserDetailsService;
import com.peters.cafecart.config.JwtService;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.shared.dtos.AuthResponse;
import com.peters.cafecart.shared.dtos.LoginRequest;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.service.CustomerService;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.shared.dtos.RefreshTokenRequest;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired CustomUserDetailsService userDetailsService;
    @Autowired CustomerService customerService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;

    @Override
    public ResponseEntity<AuthResponse> customerLogin(LoginRequest request, HttpServletResponse response) {
        System.out.println(request.email());
        CustomUserPrincipal user = userDetailsService.loadCustomerByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorShopLogin(LoginRequest request, HttpServletResponse response) {
        CustomUserPrincipal user = userDetailsService.loadVendorShopByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorLogin(LoginRequest request, HttpServletResponse response) {
        CustomUserPrincipal user = userDetailsService.loadVendorAccessAccountByUsername(request.email());
        // if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new UnauthorizedAccessException("Invalid credentials");
        return generateLoginTokens(user, response);
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

    @Override
    public ResponseEntity<AuthResponse> refreshToken(String refreshToken) {
        if (jwtService.isTokenValidForHandshake(refreshToken)) {
            String role = jwtService.extractRole(refreshToken);
            
            CustomUserPrincipal user = null;
            if (role.equals("CUSTOMER")) {
                user = userDetailsService.loadCustomerByUsername(jwtService.extractUsername(refreshToken));                 
            } else if (role.equals("VENDOR_SHOP")) {
                user = userDetailsService.loadVendorShopByUsername(jwtService.extractUsername(refreshToken));                 
            } else if (role.equals("VENDOR")) {
                user = userDetailsService.loadVendorAccessAccountByUsername(jwtService.extractUsername(refreshToken));                 
            }
            

            return generateAccessToken(user);
        } else {
            throw new UnauthorizedAccessException("Invalid Access, Please re-login");
        }
    }

    @Override
    public ResponseEntity<Boolean> isTokenValid(String token) {
        return ResponseEntity.ok(jwtService.isTokenValidForHandshake(token));
    }

    private ResponseEntity<AuthResponse> generateLoginTokens(CustomUserPrincipal user, HttpServletResponse response) {
        String access = jwtService.generateToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refresh)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 30)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(new AuthResponse(access));
    }

    private ResponseEntity<AuthResponse> generateAccessToken(CustomUserPrincipal user) {
        String access = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(access));
    }
}


