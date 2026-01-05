package com.peters.cafecart.features.AccountVerification.controller;

import com.peters.cafecart.workflows.VerifyCustomerEmailUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    private VerifyCustomerEmailUseCase verifyEmailUseCase;

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        verifyEmailUseCase.execute(token);
        return ResponseEntity.ok("Email verified successfully! Your cart has been created.");
    }
}
