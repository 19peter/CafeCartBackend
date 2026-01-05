package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.AccountVerification.entity.ResetPasswordToken;
import com.peters.cafecart.features.AccountVerification.repository.ResetPasswordTokenRepository;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.shared.notification.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestPasswordResetUseCase {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ResetPasswordTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void execute(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        // Delete existing token if any
        tokenRepository.findByCustomer(customer).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetToken = new ResetPasswordToken(token, customer);
        tokenRepository.save(resetToken);

        emailService.sendResetPasswordEmail(email, token);
    }
}
