package com.peters.cafecart.features.Authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.Authentication.service.AuthService;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.shared.dtos.Response.AuthResponse;
import com.peters.cafecart.shared.dtos.Request.LoginRequest;
import com.peters.cafecart.workflows.RequestPasswordResetUseCase;
import com.peters.cafecart.workflows.ResetPasswordUseCase;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(Constants.CURRENT_API + "/auth")
public class AuthController {
    
    @Autowired private AuthService authService;
    @Autowired private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @Autowired private ResetPasswordUseCase resetPasswordUseCase;

    @PostMapping("/login/customer")
    public ResponseEntity<AuthResponse> customerLogin(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.customerLogin(request, response);
    }

    @PostMapping("/login/vendor-shop")
    public ResponseEntity<AuthResponse> vendorShopLogin(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.vendorShopLogin(request, response);
    }

    @PostMapping("/login/vendor")
    public ResponseEntity<AuthResponse> vendorLogin(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.vendorLogin(request, response);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.adminLogin(request, response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> customerRegister(@RequestBody CustomerDto request) {
        return authService.customerRegister(request);
    }



    @PostMapping("/register/vendor")
    public ResponseEntity<?> vendorRegister(@RequestBody LoginRequest request) {
        return authService.vendorRegister(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/is-token-valid")
    public ResponseEntity<Boolean> isTokenValid(@RequestBody String token) {
        return authService.isTokenValid(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        requestPasswordResetUseCase.execute(email);
        return ResponseEntity.ok("Password reset email sent successfully if the account exists.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        resetPasswordUseCase.execute(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully.");
    }

}
