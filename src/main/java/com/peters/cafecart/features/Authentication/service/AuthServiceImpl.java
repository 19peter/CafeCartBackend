package com.peters.cafecart.features.Authentication.service;

import com.peters.cafecart.workflows.CreateCustomerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peters.cafecart.config.CustomUserDetailsService;
import com.peters.cafecart.config.JwtService;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.shared.dtos.Response.AuthResponse;
import com.peters.cafecart.shared.dtos.Request.LoginRequest;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.service.CustomerService;
import com.peters.cafecart.config.CustomUserPrincipal;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired CustomUserDetailsService userDetailsService;
    @Autowired CustomerService customerService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;
    @Autowired CreateCustomerUseCase createCustomerUseCase;
    @Value("${cookie.policy.secure}") private boolean secureCookie;
    @Value("${cookie.policy.httponly}") private boolean httpOnlyCookie;
    @Value("${cookie.policy.samesite}") private String sameSiteCookie;

    @Override
    public ResponseEntity<AuthResponse> customerLogin(LoginRequest request, HttpServletResponse response) {
        log.info("Customer login attempt for email: {}", request.email());
        CustomUserPrincipal user = userDetailsService.loadCustomerByUsername(request.email());
         if (!passwordEncoder.matches(request.password(), user.getPassword())) {
             log.warn("Invalid credentials for customer: {}", request.email());
             throw new UnauthorizedAccessException("Invalid credentials");
         }
        log.info("Customer login successful for email: {}", request.email());
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorShopLogin(LoginRequest request, HttpServletResponse response) {
        log.info("Vendor shop login attempt for email: {}", request.email());
        CustomUserPrincipal user = userDetailsService.loadVendorShopByUsername(request.email());
         if (!passwordEncoder.matches(request.password(), user.getPassword())) {
             log.warn("Invalid credentials for vendor shop: {}", request.email());
             throw new UnauthorizedAccessException("Invalid credentials");
         }
        log.info("Vendor shop login successful for email: {}", request.email());
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<AuthResponse> vendorLogin(LoginRequest request, HttpServletResponse response) {
        log.info("Vendor login attempt for email: {}", request.email());
        CustomUserPrincipal user = userDetailsService.loadVendorAccessAccountByUsername(request.email());
         if (!passwordEncoder.matches(request.password(), user.getPassword())) {
             log.warn("Invalid credentials for vendor: {}", request.email());
             throw new UnauthorizedAccessException("Invalid credentials");
         }
        log.info("Vendor login successful for email: {}", request.email());
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<AuthResponse> adminLogin(LoginRequest request, HttpServletResponse response) {
        log.info("Admin login attempt for email: {}", request.email());
        CustomUserPrincipal user = userDetailsService.loadAdminByUsername(request.email());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid credentials for admin: {}", request.email());
            throw new UnauthorizedAccessException("Invalid credentials");
        }
        log.info("Admin login successful for email: {}", request.email());
        return generateLoginTokens(user, response);
    }

    @Override
    public ResponseEntity<HttpStatus> customerRegister(CustomerDto request) {
        log.info("Registering new customer with email: {}", request.getEmail());
        createCustomerUseCase.execute(request);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }



    @Override
    public ResponseEntity<HttpStatus> vendorRegister(LoginRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<AuthResponse> refreshToken(String refreshToken) {
        if (jwtService.isTokenValidForHandshake(refreshToken)) {
            String role = jwtService.extractRole(refreshToken);
            CustomUserPrincipal user = switch (role) {
                case "CUSTOMER", "ROLE_CUSTOMER" ->
                        userDetailsService.loadCustomerByUsername(jwtService.extractUsername(refreshToken));
                case "SHOP", "ROLE_SHOP" ->
                        userDetailsService.loadVendorShopByUsername(jwtService.extractUsername(refreshToken));
                case "VENDOR", "ROLE_VENDOR" ->
                        userDetailsService.loadVendorAccessAccountByUsername(jwtService.extractUsername(refreshToken));
                case "ADMIN", "ROLE_ADMIN" ->
                        userDetailsService.loadAdminByUsername(jwtService.extractUsername(refreshToken));

                default -> {
                    log.error("Invalid role in refresh token: {}", role);
                    throw new ValidationException("Invalid Access, Please re-login");
                }
            };

            return generateAccessToken(user);
        } else {
            log.warn("Invalid refresh token attempt");
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
        String roleName = user.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                .orElse("customer");
        // Create a UNIQUE cookie name for this role
        String cookieName = roleName + "_refreshToken";

        ResponseCookie cookie = ResponseCookie.from(cookieName, refresh)
                .path("/")
                .httpOnly(httpOnlyCookie)
                .secure(secureCookie)       
                .sameSite(sameSiteCookie)  
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


