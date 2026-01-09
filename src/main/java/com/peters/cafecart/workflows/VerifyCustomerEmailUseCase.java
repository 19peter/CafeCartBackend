package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AccountVerification.entity.VerificationToken;
import com.peters.cafecart.features.AccountVerification.repository.VerificationTokenRepository;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class VerifyCustomerEmailUseCase {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartServiceImpl cartService;

    @Transactional
    public void execute(String token) {
        log.info("Executing email verification for token: {}", token.substring(0, Math.min(token.length(), 6)) + "...");
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Invalid verification token attempt: {}", token);
                    return new ValidationException("Invalid verification token");
                });

        if (verificationToken.isExpired()) {
            log.warn("Expired verification token used for customer: {}", verificationToken.getCustomer().getId());
            tokenRepository.delete(verificationToken);
            throw new ValidationException("Verification token has expired");
        }

        Customer customer = verificationToken.getCustomer();
        customer.setIsEmailVerified(true);
        customerRepository.save(customer);
        log.info("Email verified successfully for customer: {}", customer.getId());

        // Once verified, create the cart
        log.debug("Creating cart for newly verified customer: {}", customer.getId());
        cartService.createCartForNewCustomer(customer);

        // Delete the token after successful verification
        tokenRepository.delete(verificationToken);
    }
}
