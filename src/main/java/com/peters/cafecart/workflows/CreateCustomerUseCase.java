package com.peters.cafecart.workflows;

import com.peters.cafecart.features.AccountVerification.entity.VerificationToken;
import com.peters.cafecart.features.AccountVerification.repository.VerificationTokenRepository;
import com.peters.cafecart.features.Authentication.events.VerificationEmailEvent;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.shared.notification.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateCustomerUseCase {
    @Autowired CustomerServiceImpl customerService;
    @Autowired VerificationTokenRepository tokenRepository;
    @Autowired EmailService emailService;
    @Autowired ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void execute(CustomerDto customerDto) {
        Customer customer = customerService.createCustomer(customerDto);
        
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, customer);
        tokenRepository.save(verificationToken);

        applicationEventPublisher.publishEvent(new VerificationEmailEvent(customer.getEmail(), token));
    }
}
